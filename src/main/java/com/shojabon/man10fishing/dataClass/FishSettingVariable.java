package com.shojabon.man10fishing.dataClass;
import com.shojabon.mcutils.Utils.SConfigFile;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FishSettingVariable<T>{

    public ConfigurationSection config;
    public String settingId;
    T value;
    public final T defaultValue;


    public FishSettingVariable(String settingId, T defaultValue){
        this.defaultValue = defaultValue;
        this.settingId = settingId;
    }


    public Type getType(){
        return Fish.Companion.getSettingTypeMap().get(settingId);
    }

    public static Class<?> resolveBaseClass(Type type) {
        return type instanceof Class ? (Class<?>) type
                : type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getRawType()
                : null;
    }

    public Class<T> getValueClass() {
        // noinspection unchecked
        return (Class<T>) resolveBaseClass(getType());
    }

    public boolean set(T value){
        this.value = value;
        return true;
    }

    public T get(){
        if(value != null){
            return value;
        }
        try{
            if(!config.contains("fishFactors." + settingId)) return defaultValue;
            stringSet(config.getString("fishFactors." + settingId));
        }catch (Exception e){
            value = defaultValue;
        }
        return value;
    }

    public void stringSet(String value){
        Parser parser = Parser.getParser(getType());
        this.value = (T) parser.parse(this, value);
    }

    public String stringGet(){
        Parser parser = Parser.getParser(getType());
        return parser.toString(this, this.value);
    }

    public enum Parser {
        DOUBLE(Double.class, Double::parseDouble),
        BOOLEAN(Boolean.class, Boolean::parseBoolean),
        INTEGER(Integer.class, Integer::parseInt),
        FLOAT(Float.class, Float::parseFloat),
        LONG(Long.class, Long::parseLong),
        STRING(String.class, String::new),
        UUID(UUID.class, java.util.UUID::fromString),
        LIST() {
            public Object parse(FishSettingVariable context, String raw) {
                Type type = ((ParameterizedType) context.getType()).getActualTypeArguments()[0];
                Parser parser = Parser.getParser(type);

                return Stream.of(raw.split(","))
                        .map(s -> parser.parse(context, s))
                        .collect(Collectors.toList());
            }

            public String toString(FishSettingVariable context, Object value) {
                Type type = ((ParameterizedType) context.getType()).getActualTypeArguments()[0];
                Parser parser = Parser.getParser(type);

                return ((List<?>) value).stream()
                        .map(o -> parser.toString(context, o))
                        .collect(Collectors.joining(","));
            }

            public boolean accepts(Type type) {
                return List.class.isAssignableFrom(resolveBaseClass(type));
            }
        },
        ITEM_STACK() {
            public Object parse(FishSettingVariable context, String raw) {
                return SItemStack.fromBase64(raw).build();
            }

            public String toString(FishSettingVariable context, Object value) {
                return new SItemStack(((ItemStack) value)).getBase64();
            }

            public boolean accepts(Type type) {
                return ItemStack.class.isAssignableFrom(resolveBaseClass(type));
            }
        },
        YAML_CONFIG() {
            public Object parse(FishSettingVariable context, String raw) {
                return SConfigFile.loadConfigFromBase64(raw);
            }

            public String toString(FishSettingVariable context, Object value) {
                return SConfigFile.base64EncodeConfig((YamlConfiguration) value);
            }

            public boolean accepts(Type type) {
                return YamlConfiguration.class.isAssignableFrom(resolveBaseClass(type));
            }
        },
        MAPPING() {
            public Object parse(FishSettingVariable context, String raw) {
                Type keyType = ((ParameterizedType) context.getType()).getActualTypeArguments()[0];
                Type valueType = ((ParameterizedType) context.getType()).getActualTypeArguments()[1];
                Parser keyParser = Parser.getParser(keyType);
                Parser valueParser = Parser.getParser(valueType);

                return Stream.of(raw.split(",(?=[^,]*->)"))
                        .map(s -> s.split("->"))
                        .collect(Collectors.toMap(s -> keyParser.parse(context, s[0]), s -> valueParser.parse(context, s[1])));
            }

            public String toString(FishSettingVariable context, Object value) {
                Type keyType = ((ParameterizedType) context.getType()).getActualTypeArguments()[0];
                Type valueType = ((ParameterizedType) context.getType()).getActualTypeArguments()[1];
                Parser keyParser = Parser.getParser(keyType);
                Parser valueParser = Parser.getParser(valueType);

                return ((Map<?,?>) value).entrySet().stream()
                        .map(o -> keyParser.toString(context, o.getKey()) + "->" + valueParser.toString(context, o.getValue()))
                        .collect(Collectors.joining(","));
            }

            public boolean accepts(Type type) {
                return Map.class.isAssignableFrom(resolveBaseClass(type));
            }
        };


        private final Class<?> cla$$;
        private final Function<String, Object> parser;
        private final Function<Object, String> toString;

        Parser() {
            this.cla$$ = null;
            this.parser = null;
            this.toString = null;
        }

        <T> Parser(Class<T> cla$$, Function<String, T> parser) {
            this(cla$$, parser, Object::toString);
        }

        <T> Parser(Class<T> cla$$, Function<String, T> parser, Function<T, String> toString) {
            this.cla$$ = cla$$;
            this.parser = parser::apply;
            this.toString = x -> toString.apply((T) x);
        }

        public Object parse(FishSettingVariable setting, String raw) {
            Object parsed = this.parser.apply(raw);
            Objects.requireNonNull(parsed);
            return parsed;
        }

        public String toString(FishSettingVariable<?> context, Object value) {
            return this.toString.apply(value);
        }

        public boolean accepts(Type type) {
            return type instanceof Class && this.cla$$.isAssignableFrom((Class) type);
        }

        public static Parser getParser(Type type) {
            try{
                return Stream.of(values())
                        .filter(parser -> parser.accepts(type))
                        .findFirst().orElse(null);
            }catch (Exception e){
                System.out.println(e + " " + type.getTypeName());
            }
            return null;
        }
    }
}