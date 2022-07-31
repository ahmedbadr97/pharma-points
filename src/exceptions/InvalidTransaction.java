package exceptions;

public class InvalidTransaction extends Exception{

   public enum ErrorType{
        invalidMoneyIn("خطاء في اجمالي وارد الخزينه"),invalidMoneyOut("خطاء في اجمالي صادر الخزينه"),invalidCreditIn("خطاء في اجمالي وارد النقاط"),inValidCreditOut("خطاء في اجمالي صادر النقاط"),noEnoughCusCredit("لا يوجد رصيد كافي في حساب العميل"),returnGreaterThanIn("لا يمكن اسرجاع قيمه اكبر من القيمه الاصليه");
        final private String errorDescription;

        ErrorType(String errorDescription) {
            this.errorDescription = errorDescription;
        }

       public String getErrorDescription() {
           return errorDescription;
       }

       @Override
        public String toString() {
            return errorDescription;
        }
    }
    private final ErrorType errorType;
    public InvalidTransaction(ErrorType errorType) {
        super(errorType.getErrorDescription());

        this.errorType=errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
