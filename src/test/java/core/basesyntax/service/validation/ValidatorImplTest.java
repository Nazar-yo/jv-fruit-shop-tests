package core.basesyntax.service.validation;

import static org.junit.Assert.assertTrue;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.dao.strategy.FruitBalance;
import core.basesyntax.dao.strategy.FruitOperations;
import core.basesyntax.dao.strategy.FruitPurchase;
import core.basesyntax.dao.strategy.FruitSupplyOrReturn;
import core.basesyntax.dto.FruitDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidatorImplTest {
    private static final String BALANCE = "b";
    private static final String PURCHASE = "p";
    private static final String RETURN = "r";
    private static final String SUPPLY = "s";
    private static final String INVALID_OPERATION = "z";
    private static final int VALID_QUANTITY = 50;
    private static final int INVALID_QUANTITY = -20;
    private static final String FRUIT_BANANA = "banana";
    private static final String INVALID_FRUIT = "jfij12ll";
    private FruitDto validBalanceDto;
    private FruitDto validSupplyDto;
    private FruitDto validPurchaseDto;
    private FruitDto validReturnDto;
    private Validator validator;
    private FruitDao fruitDao;
    private Map<String, FruitOperations> operationsMap;

    @Before
    public void setUp() {
        operationsMap = new HashMap<>();
        operationsMap.put(BALANCE, new FruitBalance());
        operationsMap.put(PURCHASE, new FruitPurchase());
        operationsMap.put(RETURN, new FruitSupplyOrReturn());
        operationsMap.put(SUPPLY, new FruitSupplyOrReturn());
        fruitDao = new FruitDaoImpl();
        validator = new ValidatorImpl(operationsMap, fruitDao);
        validBalanceDto = new FruitDto(FRUIT_BANANA, VALID_QUANTITY, BALANCE);
        validSupplyDto = new FruitDto(FRUIT_BANANA, VALID_QUANTITY, SUPPLY);
        validPurchaseDto = new FruitDto(FRUIT_BANANA, VALID_QUANTITY, PURCHASE);
        validReturnDto = new FruitDto(FRUIT_BANANA, VALID_QUANTITY, RETURN);
    }

    @Test
    public void validate_ValidateBalanceDto_Ok() {
        validator.validateFile(validBalanceDto);
        assertTrue(fruitDao.containFruit(FRUIT_BANANA)
                && fruitDao.getQuantity(FRUIT_BANANA) == VALID_QUANTITY);
    }

    @Test
    public void validate_ValidateOperationsOrder_Ok() {
        validator.validateFile(validBalanceDto);
        validator.validateFile(validSupplyDto);
        validator.validateFile(validReturnDto);
        validator.validateFile(validPurchaseDto);
        validator.validateFile(validPurchaseDto);
        assertTrue(fruitDao.containFruit(FRUIT_BANANA)
                && fruitDao.getQuantity(FRUIT_BANANA) == VALID_QUANTITY);
    }

    @Test(expected = RuntimeException.class)
    public void validate_ValidateInvalidFruit_NotOk() {
        validator.validateFile(validBalanceDto);
        validator.validateFile(new FruitDto(INVALID_FRUIT, VALID_QUANTITY, SUPPLY));
    }

    @Test(expected = RuntimeException.class)
    public void validate_ValidateInvalidQuantity_NotOK() {
        validator.validateFile(new FruitDto(FRUIT_BANANA, INVALID_QUANTITY, BALANCE));
    }

    @Test(expected = RuntimeException.class)
    public void validate_ValidateInvalidOperation_NotOK() {
        validator.validateFile(new FruitDto(FRUIT_BANANA, VALID_QUANTITY, INVALID_OPERATION));
    }

    @Test(expected = RuntimeException.class)
    public void validate_ValidateInvalidOperationOrder_NotOK() {
        validator.validateFile(validBalanceDto);
        validator.validateFile(validPurchaseDto);
        validator.validateFile(validPurchaseDto);
    }

    @Test(expected = RuntimeException.class)
    public void validate_ValidateSupplyBeforeBalance_NotOK() {
        validator.validateFile(validSupplyDto);
        validator.validateFile(validBalanceDto);
    }

    @After
    public void tearDown() {
        fruitDao.clearStorage();
    }
}
