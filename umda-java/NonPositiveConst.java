import java.lang.Exception;

// handle the non-positive const (to estimate MU)
class NonPositiveConst extends Exception {

public NonPositiveConst(String msg){
        super(msg);
}

}
