package santander.cloud.sap.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Date;

import static santander.cloud.sap.utils.SantanderApiUtils.convertDateToStringBrFormat;


public class DateToStringBrFormatConverter implements Converter<Date, String> {

    @Override
    public String convert(Date date) {

        return convertDateToStringBrFormat(date);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Date.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }
}
