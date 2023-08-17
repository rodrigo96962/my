package santander.cloud.sap.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

public class StringToDoubleConverter implements Converter<String, Double> {

    @Override
    public Double convert(String value) {
        Double doubleValue = 0.0;
        if(!value.isEmpty()){
            doubleValue = Double.parseDouble(value.replace(",","."));
        }
        return doubleValue;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Double.class);
    }
}
