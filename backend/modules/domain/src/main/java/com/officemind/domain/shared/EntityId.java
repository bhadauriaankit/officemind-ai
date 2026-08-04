package com.officemind.domain.shared;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Type-safe identifier value object, used instead of raw {@link UUID} on
 * aggregate roots so that IDs from different aggregates (UserId, DocumentId,
 * ConversationId, ...) can never be accidentally interchanged at compile time.
 */
public final class EntityId implements Serializable {

    private final UUID value;

    private EntityId(UUID value) {
        this.value = Objects.requireNonNull(value, "id value must not be null");
    }

    public static EntityId of(UUID value) {
        return new EntityId(value);
    }

    public static EntityId of(String value) {
        return new EntityId(UUID.fromString(value));
    }

    public static EntityId generate() {
        return new EntityId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
