package trust.trustControl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.JsonArrayFormat;
import org.msgpack.jackson.dataformat.MessagePackMapper;

public final class ControlMapper {
    public static final ObjectMapper MAPPER = createMapper();

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new MessagePackMapper();
        mapper.setAnnotationIntrospector(new JsonArrayFormat());
        return mapper;
    }
}
