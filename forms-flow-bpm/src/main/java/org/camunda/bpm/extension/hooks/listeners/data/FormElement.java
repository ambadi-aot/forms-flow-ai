package org.camunda.bpm.extension.hooks.listeners.data;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
public class FormElement {
    private String op;
    private String path;
    private String value;

    public FormElement(String elementId, String value) {
        this.op = "replace";
        this.path = "/data/" + elementId;
        this.value = value;
    }

    public FormElement(String parentPath, String elementId, String value) {
        this.op = "replace";
        this.path = "/data/" + parentPath + "/" + elementId;
        this.value = value;
    }
}
