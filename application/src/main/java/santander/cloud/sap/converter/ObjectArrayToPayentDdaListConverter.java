package santander.cloud.sap.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import santander.cloud.sap.models.PaymentDda;

import java.util.ArrayList;
import java.util.List;

public class ObjectArrayToPayentDdaListConverter implements Converter<Object[], List<PaymentDda>> {

    @Override
    public List<PaymentDda> convert(Object[] objects) {
        List<PaymentDda> PaymentDdaList = new ArrayList<>();

        for(int i = 0; i < objects.length; i++){
            PaymentDdaList.add((PaymentDda) objects[i]);
        }

        return PaymentDdaList;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructArrayType(Object.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(List.class, PaymentDda.class);
    }
}
