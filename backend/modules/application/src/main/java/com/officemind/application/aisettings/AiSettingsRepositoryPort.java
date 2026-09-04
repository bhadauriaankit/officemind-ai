package com.officemind.application.aisettings;

import com.officemind.domain.aisettings.AiSettings;
import com.officemind.domain.shared.EntityId;

public interface AiSettingsRepositoryPort {

    /** Fixed id of the single AiSettings row, seeded by migration V6. */
    EntityId SETTINGS_ID = EntityId.of("00000000-0000-0000-0000-000000000001");

    AiSettings get();

    AiSettings save(AiSettings settings);
}
