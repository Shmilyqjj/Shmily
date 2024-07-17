package parquet.schema;

import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.*;
import org.apache.parquet.schema.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parquet Hive Schema
 * 兼容Hive Schema 列类型的parquet schema string生成
 */

public class ParquetDataSchema {

    // schema string打印
    public void printParquetSchemaStr() {
        MessageType schema = parquetSchema();
        System.out.println(schema.toString());
        System.out.println(schema.getFields());
    }

    public MessageType parquetSchema(){
        List<String> columnNames = new ArrayList<>();
        List<TypeInfo> columnTypes = new ArrayList<>();
        columnNames.add("name");
        columnTypes.add(TypeInfoFactory.stringTypeInfo);

        columnNames.add("age");
        columnTypes.add(TypeInfoFactory.intTypeInfo);

        columnNames.add("toatal");
        columnTypes.add(new DecimalTypeInfo(22,2));

        StructTypeInfo structTypeInfo = new StructTypeInfo();
        structTypeInfo.setAllStructFieldNames(new ArrayList<>(columnNames));
        structTypeInfo.setAllStructFieldTypeInfos(new ArrayList<>(columnTypes));
        columnNames.add("struct_test");
        columnTypes.add(structTypeInfo);

        MapTypeInfo mapTypeInfo = new MapTypeInfo();
        mapTypeInfo.setMapKeyTypeInfo(TypeInfoFactory.stringTypeInfo);
        mapTypeInfo.setMapValueTypeInfo(TypeInfoFactory.floatTypeInfo);
        columnNames.add("map_test");
        columnTypes.add(mapTypeInfo);

        ListTypeInfo listTypeInfo = new ListTypeInfo();
        listTypeInfo.setListElementTypeInfo(TypeInfoFactory.stringTypeInfo);
        columnNames.add("list_test");
        columnTypes.add(listTypeInfo);

        MessageType messageType = ParquetDataSchema.convert("hive_schema", columnNames,columnTypes);
        return messageType;
    }

    public static MessageType convert(String name, final List<String> columnNames, final List<TypeInfo> columnTypes) {
        final MessageType schema = new MessageType(name, convertTypes(columnNames, columnTypes));
        return schema;
    }

    private static Type[] convertTypes(final List<String> columnNames, final List<TypeInfo> columnTypes) {
        if (columnNames.size() != columnTypes.size()) {
            throw new IllegalStateException("Mismatched Hive columns and types. Hive columns names" +
                    " found : " + columnNames + " . And Hive types found : " + columnTypes);
        }
        final Type[] types = new Type[columnNames.size()];
        for (int i = 0; i < columnNames.size(); ++i) {
            types[i] = convertType(columnNames.get(i), columnTypes.get(i));
        }
        return types;
    }

    private static Type convertType(final String name, final TypeInfo typeInfo) {
        return convertType(name, typeInfo, Type.Repetition.OPTIONAL);
    }

    private static Type convertType(final String name, final TypeInfo typeInfo, final Type.Repetition repetition) {
        //是否是基础数据类型
        if (typeInfo.getCategory().equals(ObjectInspector.Category.PRIMITIVE)) {
            if (typeInfo.equals(TypeInfoFactory.stringTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.BINARY, repetition).as(OriginalType.UTF8)
                        .named(name);
            } else if (typeInfo.equals(TypeInfoFactory.intTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.shortTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32, repetition)
                        .as(OriginalType.INT_16).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.byteTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32, repetition)
                        .as(OriginalType.INT_8).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.longTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT64, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.doubleTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.DOUBLE, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.floatTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.FLOAT, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.booleanTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.BOOLEAN, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.binaryTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.BINARY, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.timestampTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT96, repetition).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.voidTypeInfo)) {
                throw new UnsupportedOperationException("Void type not implemented");
            } else if (typeInfo.getTypeName().toLowerCase().startsWith(
                    serdeConstants.CHAR_TYPE_NAME)) {
                return Types.optional(PrimitiveType.PrimitiveTypeName.BINARY).as(OriginalType.UTF8)
                        .named(name);
            } else if (typeInfo.getTypeName().toLowerCase().startsWith(
                    serdeConstants.VARCHAR_TYPE_NAME)) {
                return Types.optional(PrimitiveType.PrimitiveTypeName.BINARY).as(OriginalType.UTF8)
                        .named(name);
            } else if (typeInfo instanceof DecimalTypeInfo) {
                DecimalTypeInfo decimalTypeInfo = (DecimalTypeInfo) typeInfo;
                int prec = decimalTypeInfo.precision();
                int scale = decimalTypeInfo.scale();
                int bytes = ParquetHiveSerDe.PRECISION_TO_BYTE_COUNT[prec - 1];
                return Types.optional(PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY).length(bytes).as(OriginalType.DECIMAL).
                        scale(scale).precision(prec).named(name);
            } else if (typeInfo.equals(TypeInfoFactory.dateTypeInfo)) {
                return Types.primitive(PrimitiveType.PrimitiveTypeName.INT32, repetition).as(OriginalType.DATE).named
                        (name);
            } else if (typeInfo.equals(TypeInfoFactory.unknownTypeInfo)) {
                throw new UnsupportedOperationException("Unknown type not implemented");
            } else {
                throw new IllegalArgumentException("Unknown type: " + typeInfo);
            }
        } else if (typeInfo.getCategory().equals(ObjectInspector.Category.LIST)) {
            return convertArrayType(name, (ListTypeInfo) typeInfo);
        } else if (typeInfo.getCategory().equals(ObjectInspector.Category.STRUCT)) {
            return convertStructType(name, (StructTypeInfo) typeInfo);
        } else if (typeInfo.getCategory().equals(ObjectInspector.Category.MAP)) {
            return convertMapType(name, (MapTypeInfo) typeInfo);
        } else if (typeInfo.getCategory().equals(ObjectInspector.Category.UNION)) {
            throw new UnsupportedOperationException("Union type not implemented");
        } else {
            throw new IllegalArgumentException("Unknown type: " + typeInfo);
        }
    }

    // An optional group containing a repeated anonymous group "bag", containing
    // 1 anonymous element "array_element"
    @SuppressWarnings("deprecation")
    private static GroupType convertArrayType(final String name, final ListTypeInfo typeInfo) {
        final TypeInfo subType = typeInfo.getListElementTypeInfo();
        return new GroupType(Type.Repetition.OPTIONAL, name, OriginalType.LIST, new GroupType(Type.Repetition.REPEATED,
                ParquetHiveSerDe.ARRAY.toString(), convertType("array_element", subType)));
    }

    // An optional group containing multiple elements
    private static GroupType convertStructType(final String name, final StructTypeInfo typeInfo) {
        final List<String> columnNames = typeInfo.getAllStructFieldNames();
        final List<TypeInfo> columnTypes = typeInfo.getAllStructFieldTypeInfos();
        return new GroupType(Type.Repetition.OPTIONAL, name, convertTypes(columnNames, columnTypes));

    }

    // An optional group containing a repeated anonymous group "map", containing
    // 2 elements: "key", "value"
    private static GroupType convertMapType(final String name, final MapTypeInfo typeInfo) {
        final Type keyType = convertType(ParquetHiveSerDe.MAP_KEY.toString(),
                typeInfo.getMapKeyTypeInfo(), Type.Repetition.REQUIRED);
        final Type valueType = convertType(ParquetHiveSerDe.MAP_VALUE.toString(),
                typeInfo.getMapValueTypeInfo());
        return ConversionPatterns.mapType(Type.Repetition.OPTIONAL, name, keyType, valueType);
    }
}
