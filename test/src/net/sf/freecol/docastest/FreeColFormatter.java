package net.sf.freecol.docastest;

import org.sfvl.docformatter.asciidoc.AsciidocFormatter;

public class FreeColFormatter extends AsciidocFormatter {
    public String image(String path, String title) {
        return String.format("image:%s[title=\"%s\"]",
                path,
                title);
    }
}
