package untrusted.payload;

import java.io.Serial;
import java.io.Serializable;

public record UnexpectedPayload(String value) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
