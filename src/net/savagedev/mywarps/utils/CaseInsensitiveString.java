package net.savagedev.mywarps.utils;

public class CaseInsensitiveString {
    private final String string;

    public CaseInsensitiveString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseInsensitiveString && this.toString().equalsIgnoreCase((obj).toString());
    }

    @Override
    public String toString() {
        return this.string;
    }
}
