package com.example.myapplication.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalAnnotationManager {

    private static LocalAnnotationManager instance;
    private Map<String, Map<String, AnnotatedItem>> contextMappings;

    public static LocalAnnotationManager getInstance() {
        if (instance == null) {
            instance = new LocalAnnotationManager();
        }
        return instance;
    }

    private LocalAnnotationManager() {
        initializeMappings();
    }

    private void initializeMappings() {
        contextMappings = new HashMap<>();

        // === SOLDADOR (welder) ===
        Map<String, AnnotatedItem> welderMap = new HashMap<>();
        welderMap.put("welding mask", new AnnotatedItem(
                "welding mask", "Máscara de Soldar", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 1
        ));
        welderMap.put("welding gear", new AnnotatedItem(
                "welding gear", "Equipo de Soldadura", "torso",
                0.5f, 0.4f, 0.35f, 0.4f, 2
        ));
        welderMap.put("gloves", new AnnotatedItem(
                "gloves", "Guantes", "hands",
                0.5f, 0.75f, 0.4f, 0.2f, 3
        ));
        welderMap.put("safety mask", new AnnotatedItem(
                "safety mask", "Mascarilla", "face",
                0.5f, 0.25f, 0.2f, 0.15f, 2
        ));
        contextMappings.put("welder", welderMap);

        // === MÉDICO (medical) ===
        Map<String, AnnotatedItem> medicalMap = new HashMap<>();
        medicalMap.put("face mask", new AnnotatedItem(
                "face mask", "Mascarilla", "face",
                0.5f, 0.25f, 0.2f, 0.15f, 1
        ));
        medicalMap.put("medical gloves", new AnnotatedItem(
                "medical gloves", "Guantes Médicos", "hands",
                0.5f, 0.75f, 0.4f, 0.2f, 1
        ));
        medicalMap.put("gown", new AnnotatedItem(
                "gown", "Bata Médica", "torso",
                0.5f, 0.4f, 0.35f, 0.5f, 2
        ));
        medicalMap.put("face shield", new AnnotatedItem(
                "face shield", "Protector Facial", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 2
        ));
        contextMappings.put("medical", medicalMap);

        // === GUARDIA (security_guard) ===
        Map<String, AnnotatedItem> securityMap = new HashMap<>();
        securityMap.put("uniform", new AnnotatedItem(
                "uniform", "Uniforme", "torso",
                0.5f, 0.4f, 0.3f, 0.4f, 1
        ));
        securityMap.put("bulletproof vest", new AnnotatedItem(
                "bulletproof vest", "Chaleco Antibalas", "torso",
                0.5f, 0.4f, 0.35f, 0.45f, 1
        ));
        securityMap.put("radio", new AnnotatedItem(
                "radio", "Radio", "torso",
                0.8f, 0.45f, 0.1f, 0.15f, 3
        ));
        securityMap.put("belt", new AnnotatedItem(
                "belt", "Cinturón", "waist",
                0.5f, 0.6f, 0.35f, 0.1f, 3
        ));
        contextMappings.put("security_guard", securityMap);

        // === CONSTRUCCIÓN (construction) ===
        Map<String, AnnotatedItem> constructionMap = new HashMap<>();
        constructionMap.put("helmet", new AnnotatedItem(
                "helmet", "Casco", "head",
                0.5f, 0.15f, 0.25f, 0.2f, 1
        ));
        constructionMap.put("vest", new AnnotatedItem(
                "vest", "Chaleco Reflectante", "torso",
                0.5f, 0.4f, 0.3f, 0.4f, 1
        ));
        constructionMap.put("boots", new AnnotatedItem(
                "boots", "Botas de Seguridad", "feet",
                0.5f, 0.9f, 0.25f, 0.15f, 2
        ));
        constructionMap.put("gloves", new AnnotatedItem(
                "gloves", "Guantes", "hands",
                0.5f, 0.75f, 0.4f, 0.2f, 3
        ));
        contextMappings.put("construction", constructionMap);
    }

    /**
     * Obtiene las anotaciones para elementos faltantes
     */
    public List<AnnotatedItem> getAnnotationsForMissingItems(List<String> missingItems, String context) {
        List<AnnotatedItem> annotations = new ArrayList<>();

        if (missingItems == null || missingItems.isEmpty()) {
            return annotations;
        }

        Map<String, AnnotatedItem> contextMap = contextMappings.get(context);
        if (contextMap == null) {
            contextMap = contextMappings.get("welder"); // Default
        }

        for (String itemName : missingItems) {
            String normalizedName = normalizeItemName(itemName);
            AnnotatedItem item = findMatchingItem(normalizedName, contextMap);

            if (item != null) {
                annotations.add(item);
            } else {
                // Elemento genérico si no se encuentra
                annotations.add(new AnnotatedItem(
                        itemName, itemName, "center",
                        0.5f, 0.5f, 0.3f, 0.3f, 3
                ));
            }
        }

        return annotations;
    }

    private String normalizeItemName(String itemName) {
        return itemName.toLowerCase().trim();
    }

    private AnnotatedItem findMatchingItem(String itemName, Map<String, AnnotatedItem> contextMap) {
        // Buscar coincidencia exacta
        if (contextMap.containsKey(itemName)) {
            return contextMap.get(itemName);
        }

        // Buscar por palabras clave
        for (Map.Entry<String, AnnotatedItem> entry : contextMap.entrySet()) {
            if (itemName.contains(entry.getKey()) || entry.getKey().contains(itemName)) {
                return entry.getValue();
            }
        }

        // Buscar por sinónimos
        if (itemName.contains("mask")) {
            return contextMap.get("face mask") != null ? contextMap.get("face mask") :
                    contextMap.get("welding mask");
        }
        if (itemName.contains("glove")) {
            return contextMap.get("gloves") != null ? contextMap.get("gloves") :
                    contextMap.get("medical gloves");
        }
        if (itemName.contains("helmet") || itemName.contains("casco")) {
            return contextMap.get("helmet");
        }
        if (itemName.contains("vest") || itemName.contains("chaleco")) {
            return contextMap.get("vest") != null ? contextMap.get("vest") :
                    contextMap.get("bulletproof vest");
        }
        if (itemName.contains("boot") || itemName.contains("bota")) {
            return contextMap.get("boots");
        }
        if (itemName.contains("gear") || itemName.contains("equipo")) {
            return contextMap.get("welding gear");
        }

        return null;
    }

    /**
     * Obtiene el nombre del contexto en español
     */
    public String getContextDisplayName(String context) {
        switch (context) {
            case "welder": return "Soldador";
            case "medical": return "Médico/Enfermera";
            case "security_guard": return "Guardia de Seguridad";
            case "construction": return "Construcción/Obra";
            default: return "General";
        }
    }
}