package com.tap.framework.healing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import org.openqa.selenium.WebElement;

/**
 * Fingerprint of an element taken while its locator still worked. It is what the healing engine
 * compares the live DOM against once the original locator stops matching.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementSnapshot {

    private static final int MAX_TEXT = 60;

    private String tag = "";
    private String id = "";
    private String name = "";
    private String classes = "";
    private String type = "";
    private String placeholder = "";
    private String text = "";

    public ElementSnapshot() {
    }

    public static ElementSnapshot of(WebElement element) {
        ElementSnapshot snapshot = new ElementSnapshot();
        snapshot.tag = value(element.getTagName());
        snapshot.id = attribute(element, "id");
        snapshot.name = attribute(element, "name");
        snapshot.classes = attribute(element, "class");
        snapshot.type = attribute(element, "type");
        snapshot.placeholder = attribute(element, "placeholder");
        String elementText = value(element.getText()).trim();
        snapshot.text = elementText.length() > MAX_TEXT ? elementText.substring(0, MAX_TEXT) : elementText;
        return snapshot;
    }

    private static String attribute(WebElement element, String attribute) {
        return value(element.getAttribute(attribute)).trim();
    }

    private static String value(String candidate) {
        return candidate == null ? "" : candidate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = value(tag);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = value(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = value(name);
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = value(classes);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = value(type);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = value(placeholder);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = value(text);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ElementSnapshot)) {
            return false;
        }
        ElementSnapshot that = (ElementSnapshot) other;
        return tag.equals(that.tag) && id.equals(that.id) && name.equals(that.name)
                && classes.equals(that.classes) && type.equals(that.type)
                && placeholder.equals(that.placeholder) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, id, name, classes, type, placeholder, text);
    }

    @Override
    public String toString() {
        return "<" + tag + " id='" + id + "' name='" + name + "' class='" + classes + "'>";
    }
}
