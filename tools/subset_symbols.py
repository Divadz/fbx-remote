"""
Sous-ensemble de la police Material Symbols pour le build release.

Usage :
    python subset_symbols.py <police_complete.ttf> <fichier.codepoints> <dossier_sources_kt> <police_sortie.ttf>

Le script cherche dans les sources Kotlin toutes les références `Sym.nom` (y compris
`Sym._10k` et Sym.`class`), les convertit en codepoints grâce au fichier officiel, puis
découpe la police avec fontTools en ne gardant que ces glyphes. Les axes variables
(FILL, wght, GRAD, opsz) sont conservés.

Prérequis : `pip install fonttools`.
"""

import re
import sys
from pathlib import Path

try:
    from fontTools import subset
except ImportError:  # message clair plutôt qu'une trace Python
    sys.stderr.write(
        "fontTools est introuvable. Installez-le avec :  python -m pip install fonttools\n"
    )
    sys.exit(2)

# Symboles toujours inclus, même s'ils ne sont pas référencés statiquement.
ALWAYS = {"settings"}

SYM_REF = re.compile(r"\bSym\.(?:`([A-Za-z0-9_]+)`|([A-Za-z0-9_]+))")
# Commentaires Kotlin (// … et /* … */), retirés avant l'analyse pour ne pas
# compter les exemples de la documentation comme des symboles utilisés.
COMMENTS = re.compile(r"/\*.*?\*/|//[^\n]*", re.DOTALL)


def load_codepoints(path: Path) -> dict[str, int]:
    table: dict[str, int] = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        parts = line.split()
        if len(parts) == 2:
            table.setdefault(parts[0], int(parts[1], 16))
    return table


def used_symbol_names(src_dir: Path) -> set[str]:
    names: set[str] = set()
    for kt in src_dir.rglob("*.kt"):
        if kt.name == "Sym.kt":  # le fichier généré définit tout, il ne « consomme » rien
            continue
        code = COMMENTS.sub("", kt.read_text(encoding="utf-8"))
        for m in SYM_REF.finditer(code):
            ident = m.group(1) or m.group(2)
            # `_10k` dans le code correspond à `10k` dans le fichier de codepoints
            if ident.startswith("_") and len(ident) > 1 and ident[1].isdigit():
                ident = ident[1:]
            names.add(ident)
    return names


def main() -> int:
    if len(sys.argv) != 5:
        sys.stderr.write(__doc__)
        return 2
    font_in, codepoints_file, src_dir, font_out = (Path(a) for a in sys.argv[1:])

    table = load_codepoints(codepoints_file)
    names = used_symbol_names(src_dir) | ALWAYS
    unknown = sorted(n for n in names if n not in table)
    if unknown:
        # Un nom inconnu ne compilerait pas côté Kotlin : on signale sans bloquer.
        sys.stderr.write("Avertissement : symboles ignorés (absents des codepoints) : %s\n" % ", ".join(unknown))
        names -= set(unknown)
    unicodes = sorted(table[n] for n in names)

    options = subset.Options()
    options.notdef_outline = True
    options.hinting = False
    options.layout_features = []          # pas de ligatures : on adresse les glyphes par codepoint
    options.name_IDs = ["*"]              # conserve le nom de la police
    options.drop_tables += ["DSIG"]

    font = subset.load_font(str(font_in), options)
    subsetter = subset.Subsetter(options)
    subsetter.populate(unicodes=unicodes)
    subsetter.subset(font)
    font_out.parent.mkdir(parents=True, exist_ok=True)
    subset.save_font(font, str(font_out), options)

    before = font_in.stat().st_size
    after = font_out.stat().st_size
    print(
        "Material Symbols : %d symboles conservés (%s), %.1f Mo -> %.0f ko"
        % (len(names), ", ".join(sorted(names)), before / 1048576, after / 1024)
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
