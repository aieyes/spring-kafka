package org.apache.kafka.common.protocol.types;

public class TaggedFieldsSection extends Field {
    private static final String NAME = "_tagged_fields";
    private static final String DOC_STRING = "The tagged fields";

    /**
     * Create a new TaggedFieldsSection with the given tags and fields.
     *
     * @param fields This is an array containing Integer tags followed
     *               by associated Field objects.
     * @return The new {@link TaggedFieldsSection}
     */
    public static TaggedFieldsSection of(Object... fields) {
        return new TaggedFieldsSection(TaggedFields.of(fields));
    }

    public TaggedFieldsSection(Type type) {
        super(-1, NAME, type, DOC_STRING, false, null);
    }
}