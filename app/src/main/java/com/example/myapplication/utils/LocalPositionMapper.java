// app/utils/LocalPositionMapper.java
package com.example.myapplication.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalPositionMapper {

    private Map<String, Map<String, AnnotatedItem>> contextPositions;

    public LocalPositionMapper() {
        initializePositions();
    }

    private void initializePositions() {
        contextPositions = new HashMap<>();

        // Soldador
        Map<String, AnnotatedItem> welderMap = new HashMap<>();
        welderMap.put("welding mask", new AnnotatedItem(
                "welding mask", "Máscara de soldar", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 1
        ));
        welderMap.put("welding gear", new AnnotatedItem(
                "welding gear", "Equipo de soldadura", "torso",
                0.35f, 0.35f, 0.3f, 0.4f, 2
        ));
        welderMap.put("gloves", new AnnotatedItem(
                "gloves", "Guantes", "hands",
                0.3f, 0.7f, 0.4f, 0.15f, 3
        ));
        welderMap.put("safety mask", new AnnotatedItem(
                "safety mask", "Máscara de seguridad", "face",
                0.5f, 0.2f, 0.2f, 0.15f, 2
        ));
        contextPositions.put("welder", welderMap);

        // Médico
        Map<String, AnnotatedItem> medicalMap = new HashMap<>();
        medicalMap.put("face mask", new AnnotatedItem(
                "face mask", "Mascarilla", "face",
                0.5f, 0.2f, 0.25f, 0.15f, 1
        ));
        medicalMap.put("medical gloves", new AnnotatedItem(
                "medical gloves", "Guantes médicos", "hands",
                0.3f, 0.7f, 0.4f, 0.15f, 1
        ));
        medicalMap.put("gown", new AnnotatedItem(
                "gown", "Bata médica", "body",
                0.35f, 0.35f, 0.3f, 0.4f, 2
        ));
        medicalMap.put("face shield", new AnnotatedItem(
                "face shield", "Protector facial", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 2
        ));
        contextPositions.put("medical", medicalMap);

        // Guardia de seguridad
        Map<String, AnnotatedItem> securityMap = new HashMap<>();
        securityMap.put("uniform", new AnnotatedItem(
                "uniform", "Uniforme", "torso",
                0.35f, 0.35f, 0.3f, 0.4f, 1
        ));
        securityMap.put("bulletproof vest", new AnnotatedItem(
                "bulletproof vest", "Chaleco antibalas", "torso",
                0.35f, 0.35f, 0.3f, 0.4f, 1
        ));
        securityMap.put("radio", new AnnotatedItem(
                "radio", "Radio", "torso",
                0.7f, 0.4f, 0.1f, 0.15f, 3
        ));
        securityMap.put("belt", new AnnotatedItem(
                "belt", "Cinturón", "waist",
                0.35f, 0.6f, 0.3f, 0.1f, 3
        ));
        contextPositions.put("security_guard", securityMap);

        // Construcción
        Map<String, AnnotatedItem> constructionMap = new HashMap<>();
        constructionMap.put("helmet", new AnnotatedItem(
                "helmet", "Casco", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 1
        ));
        constructionMap.put("vest", new AnnotatedItem(
                "vest", "Chaleco reflectante", "torso",
                0.35f, 0.35f, 0.3f, 0.4f, 1
        ));
        constructionMap.put("boots", new AnnotatedItem(
                "boots", "Botas de seguridad", "feet",
                0.4f, 0.85f, 0.2f, 0.1f, 2
        ));
        constructionMap.put("gloves", new AnnotatedItem(
                "gloves", "Guantes", "hands",
                0.3f, 0.7f, 0.4f, 0.15f, 3
        ));
        contextPositions.put("construction", constructionMap);
    }

    public List<AnnotatedItem> getPositionsForMissingItems(List<String> missingItemNames, String context) {
        List<AnnotatedItem> annotatedItems = new ArrayList<>();

        Map<String, AnnotatedItem> contextMap = contextPositions.get(context);
        if (contextMap == null) {
            // Si no encuentra el contexto, usar soldador por defecto
            contextMap = contextPositions.get("welder");
        }

        for (String itemName : missingItemNames) {
            // Buscar coincidencia parcial (por si los nombres no son exactos)
            for (Map.Entry<String, AnnotatedItem> entry : contextMap.entrySet()) {
                if (itemName.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                        entry.getKey().toLowerCase().contains(itemName.toLowerCase())) {
                    annotatedItems.add(entry.getValue());
                    break;
                }
            }
        }

        return annotatedItems;
    }
}