package santander.cloud.sap.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import santander.cloud.sap.models.DebitAccount;

import java.util.ArrayList;
import java.util.List;

public class ObjectArrayToDebtAccountListConverter implements Converter<Object[], List<DebitAccount>> {

    @Override
    public List<DebitAccount> convert(Object[] objects) {
        List<DebitAccount> debitAccountList = new ArrayList<>();

        for(int i = 0; i < objects.length; i++){
            debitAccountList.add((DebitAccount) objects[i]);
        }

        return debitAccountList;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructArrayType(Object.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(List.class, DebitAccount.class);
    }
}
