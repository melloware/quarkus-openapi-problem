package io.quarkiverse.openapi.problem.validation;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Validation constraint violation details")
public final class Violation {

    /** The field that failed validation */
    @Schema(description = "The field that failed validation", examples = "#/profile/email")
    private String pointer;

    /** Location where the validation error occurred */
    @Schema(description = "Location where the validation error occurred such as query, path, header, form, body", examples = {
            "query", "path", "header", "form", "body" })
    private String in;

    /** Description of the validation error */
    @Schema(description = "Description of the validation error", examples = "Invalid email format")
    private String detail;

    public enum In {
        query,
        path,
        header,
        form,
        body,
        unknown() {
            @Override
            protected String serialize() {
                return "?";
            }
        };

        public MessageSupplier field(String field) {
            return message -> new Violation(field, this.serialize(), message);
        }

        protected String serialize() {
            return name();
        }
    }

    public interface MessageSupplier {
        Violation message(String message);
    }
}