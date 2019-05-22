public class TypeCheckException extends Exception{

	public TypeCheckException(){
		super();
	}

	public String getMessage() {
		return "Type Checking failed";
	}
}