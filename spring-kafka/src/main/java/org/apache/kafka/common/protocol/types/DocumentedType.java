package org.apache.kafka.common.protocol.types;

/**
 * A Type that can return its description for documentation purposes.
 */
public abstract class DocumentedType extends Type {

    /**
     * Short name of the type to identify it in documentation;
     *
     * @return the name of the type
     */
    public abstract String typeName();

    /**
     * Documentation of the Type.
     *
     * @return details about valid values, representation
     */
    public abstract String documentation();

    @Override
    public String toString() {
        return typeName();
    }
}