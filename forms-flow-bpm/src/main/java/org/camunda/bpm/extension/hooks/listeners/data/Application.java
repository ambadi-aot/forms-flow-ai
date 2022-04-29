package org.camunda.bpm.extension.hooks.listeners.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application.
 * Transfer objects for Application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application{
    private String applicationStatus;
    private String formUrl;
    private String submittedBy;
}
