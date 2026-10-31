package com.royalsmp.tab.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtils() {}

    /**
     * Translates a string containing MiniMessage tags, Hex codes (&#RRGGBB), or legacy & codes into an Adventure Component.
     */
    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        // Convert &#RRGGBB to MiniMessage <#RRGGBB>
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        String text = sb.toString();

        // If the string contains legacy '&' codes, translate them or convert to component
        if (text.contains("&")) {
            // First parse legacy ampersand into a component, then re-merge or deserialize
            Component legacyComp = LEGACY_AMPERSAND.deserialize(text);
            String miniText = MINI_MESSAGE.serialize(legacyComp);
            return MINI_MESSAGE.deserialize(miniText);
        }

        // Parse with MiniMessage
        try {
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            return LEGACY_SECTION.deserialize(text);
        }
    }
}
