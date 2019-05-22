import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.lang.*;

public class TypeCheckVisitor extends GJDepthFirst<String,ArgumentsForVisit> {

	public String visit(Goal n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); //MainClass
			n.f1.accept(this, ar); //TypeDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(MainClass n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			ar.curr_class = n.f1.f0.toString(); //class name
			ar.curr_method = "main";

			n.f14.accept(this, ar); //VarDeclaration
			n.f15.accept(this, ar); //Statement
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(VarDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			int choice = n.f0.f0.which;

			if ( choice == 3 ){  //Identifier dld ena onoma klashs
				String tmp = ar.curr_class;
				ar.curr_class = "check decls";
				n.f0.accept(this, ar); //Type
				ar.curr_class = tmp;
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(TypeDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		String result = null;
		try{
			result = n.f0.accept(this, ar); //ClassDeclaration or ClassExtendsDeclaration,WhileStatement,
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}

	public String visit(ClassDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String class_name = n.f1.f0.toString(); //Class Identifier
			ar.curr_class = class_name;

			n.f3.accept(this, ar); //VarDeclaration
			n.f4.accept(this, ar); //MethodDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ClassExtendsDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String class_name = n.f1.f0.toString(); // Class Identifier
			String parent_class = n.f3.f0.toString(); //Parent Class

			if ( class_name.equals(parent_class) ){ //idio onoma me thn uperklash ths
				System.out.println("error: Class "+parent_class+" has already defined");
				throw new TypeCheckException();
			}
			if ( !ar.m.containsKey(parent_class) ){
				System.out.println("error: Class "+parent_class+" is not defined");
				throw new TypeCheckException();
			}
			ar.curr_class = class_name;

			n.f5.accept(this, ar); //VarDeclaration
			n.f6.accept(this, ar); //MethodDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;	
	}

	public String visit(MethodDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String ret_type = n.f1.accept(this, ar); //Type
			String method_name = n.f2.f0.toString(); //Identifier (method name)

			n.f7.accept(this, ar); //VarDeclaration
			ar.curr_method = method_name;
			String stmt = n.f8.accept(this, ar); //Statement
			String ret_exp = n.f10.accept(this, ar); //Expression epistrofhs

			if ( !ret_type.equals(ret_exp) ){
				System.out.println("error: '"+method_name+"' returns "+ret_exp+" instead of "+ret_type);
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	/*** STATEMENTS ***/
	public String visit(Statement n, ArgumentsForVisit ar) throws TypeCheckException {
		//Block, AssignmentStatement, ArrayAssignmentStatement, IfStatement, PrintStatement
		try{
			n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(Block n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f1.accept(this, ar); //Statement
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(AssignmentStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		//identifier = Expression
		try{
			String ident = n.f0.accept(this, ar); //Identifier
			String exp = n.f2.accept(this, ar); //Expression
			//System.out.println("\tAssigning "+ident+" to "+exp);

			if ( !ident.equals(exp) ){
				if ( !ar.inheritanceAssignment(ident, exp) ){ // gia thn periptwsh ident = new ident()
					System.out.println("error: VVVariable '"+n.f0.f0.toString()+"' is type of "+ident+" and assigned to "+exp);
					throw new TypeCheckException();
				}
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ArrayAssignmentStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		//Identifier [ Expression ] = Expression
		try{
			String ident = n.f0.accept(this, ar); //Identifier (type)
			String arr_exp = n.f2.accept(this,ar); //Expression in [ ] (must be int)
			String asgn_exp = n.f5.accept(this,ar); //Expression to be assigned

			if ( !ident.equals("int[]") ){ //elegxos gia Identifier
				System.out.println("error: Variable "+n.f0.f0.toString()+" is not type of 'int[]'");
				throw new TypeCheckException();
			}
			if ( !arr_exp.equals("int") ){ //elegxos gia Expression stis agkules []
				System.out.println("error: Expression in '[ ]' must be type of int");
				throw new TypeCheckException();
			}
			if ( !asgn_exp.equals("int") ){ //elegxos gia Expression pou anatithetai 
				System.out.println("error: Invalid assignment to variable '"+n.f0.f0.toString()+"'");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}
	
	public String visit(IfStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String exp = n.f2.accept(this, ar); //Expression in if( exp )
			if ( !exp.equals("boolean") ){
				System.out.println("error: Expected boolean experssion in 'if( )'");
				throw new TypeCheckException();
			}
			String stmt1 = n.f4.accept(this, ar); //Statement in if block
			String stmt2 = n.f6.accept(this, ar); //Statement in else block
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(WhileStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String exp = n.f2.accept(this, ar); //Expression in while( exp )
			if ( !exp.equals("boolean") ){
				System.out.println("error: Expected boolean experssion in 'while( )'");
				throw new TypeCheckException();
			}
			String stmt = n.f4.accept(this, ar); //Statement in while block
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(PrintStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String exp = n.f2.accept(this, ar); //Expression in System.out.println( exp )
			if ( !exp.equals("int") && !exp.equals("boolean") ){
				System.out.println("error: System.out.println() expected type of int or boolean but "+exp+" given");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	/*** EXPRESSIONS ***/

	//Expression : AndExpression,CompareExpression,PlusExpression,MinusExpression,TimesExpression
	//ArrayLookup,ArrayLength,MessageSend,Clause
	public String visit(Expression n, ArgumentsForVisit ar) throws TypeCheckException {
		String result = null;
		try{
			result = n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}
	public String visit(AndExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String clause1 = n.f0.accept(this,ar); //Clause
			String clause2 = n.f2.accept(this,ar); //Clause

			if( clause1.equals("boolean") && clause2.equals("boolean") ){
				return "boolean";
			}
			else{
				System.out.println("error: Operator && needs boolean expressions");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(CompareExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String prim_exp1 = n.f0.accept(this, ar); //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar); //PrimaryExpression

			if ( prim_exp1.equals(prim_exp2) && prim_exp1.equals("int") ) return "boolean";
			else{
				System.out.println("error: comparison between different types");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(PlusExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String prim_exp1 = n.f0.accept(this, ar); //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar); //PrimaryExpression

			if ( prim_exp1.equals(prim_exp2) && prim_exp1.equals("int") ) return prim_exp1;
			else{
				System.out.println("error: addition of two different types");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();	
		}	
	}

	public String visit(MinusExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String prim_exp1 = n.f0.accept(this, ar); //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar); //PrimaryExpression

			if ( prim_exp1.equals(prim_exp2) && prim_exp1.equals("int") ) return prim_exp1;
			else{
				System.out.println("error: substraction of two different types");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(TimesExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String prim_exp1 = n.f0.accept(this, ar); //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar); //PrimaryExpression
			if ( prim_exp1.equals(prim_exp2) && prim_exp1.equals("int") ) return prim_exp1;
			else{
				System.out.println("error: multiplication of two different types");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(ArrayLookup n, ArgumentsForVisit ar) throws TypeCheckException {
		//PrimaryExpression '[' PrimaryExpression ']'
		try{
			String prim_exp1 = n.f0.accept(this, ar); //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar); //PrimaryExpression

			if ( !prim_exp1.equals("int[]") ){
				System.out.println("error: Array operator assigned in non array type object");
				throw new TypeCheckException();
			}
			if ( !prim_exp2.equals("int") ){
				System.out.println("error: Expression in '[ ]' must be type of int");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return "int";
	}

	public String visit(ArrayLength n, ArgumentsForVisit ar) throws TypeCheckException {
		//PrimaryExpression . length
		try{
			String prim_exp = n.f0.accept(this, ar); //PrimaryExpression
			if ( !prim_exp.equals("int[]") ){
				System.out.println("error: Method 'length' assigned to '"+prim_exp+"', exprected type of 'int[]'");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return "int";
	}

	public String visit(MessageSend n, ArgumentsForVisit ar) throws TypeCheckException {
		//PrimaryExpression . Identifier ( ExpressionList )
		//ar.arg_list.clear();
		String ret_type = null;
		try{
			ar.arg_list.push(new ArrayList<String>());

			String prim_exp = n.f0.accept(this, ar); //PrimaryExpression
			String ident = n.f2.f0.toString(); //Identifier
			String actual_class = ar.objectOfMethod(prim_exp, ident);
			if ( actual_class.equals("not found") ){ //h methodos 'ident' DEN anoikei sthn prim_exp h se gonikh
				System.out.println("error: Class '"+prim_exp+"' has not method named '"+ident+"'");
				throw new TypeCheckException();
			}
			//arguments
			String exp_list = n.f4.accept(this, ar); //ExpressionList

			if ( !ar.validArguments(actual_class, ident) ){
				//System.out.println("error: Invalid arguments at method '"+ident+"'");
				throw new TypeCheckException();
			}

			// ELEGXOS ORISMATWN
			ret_type = ar.returnTupeOfMethod(prim_exp, ident);
			if ( ret_type.equals("not found") ){
				System.out.println("error on return type of "+ident+" ...");
				throw new TypeCheckException();
			}
			ar.arg_list.pop();
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return ret_type;
	}

	public String visit(ExpressionList n, ArgumentsForVisit ar) throws TypeCheckException {
		String exp = null;
		try{
			exp = n.f0.accept(this, ar); //Expression
			if( exp == null ) return null;
			ar.arg_list.peek().add(exp);
			String exp_tail = n.f1.accept(this, ar); //ExpressionTail
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return exp;
	}

	public String visit(ExpressionTail n, ArgumentsForVisit ar) throws TypeCheckException {
		String result = null;
		try{
			result = n.f0.accept(this, ar); //ExpressionTerm
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}

	public String visit(ExpressionTerm n, ArgumentsForVisit ar) throws TypeCheckException {
		String result = null;
		try{
			result = n.f1.accept(this, ar); //Expression
			ar.arg_list.peek().add(result);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}

	public String visit(Clause n, ArgumentsForVisit ar) throws TypeCheckException { //thelei kai alla pragmata
		String result = null;
		try{
			result = n.f0.accept(this, ar); // NotExpression or PrimaryExpression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result; //epistrefei tupo
	}


	public String visit(PrimaryExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		/**
			IntegerLiteral, TrueLiteral, FalseLiteral, Identifier, ThisExpression,
			ArrayAllocationExpression,   AllocationExpression,   BracketExpression
		**/
		String result = null;
		try{
			result = n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result; //epistrefei tupo
	}

	public String visit(NotExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		String clause = null;
		try{
			clause = n.f1.accept(this, ar); //Clause
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return clause;
	}

	/******** TERMINALS **********/

	public String visit(IntegerLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String result = n.f0.accept(this, ar); //NodeToken
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return "int";
	}

	public String visit(TrueLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "boolean";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(FalseLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "boolean";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(Identifier n, ArgumentsForVisit ar) throws TypeCheckException {
		/*an h curr_class exei timh check decls shmainei oti theloume na 
		epistrepsoume ton idio ton identifier kai oxi ton tupo tou */
		String result = null;
		try{
			result = n.f0.accept(this, ar); //NodeToken
			if ( ar.curr_class.equals("check decls") ){ //gia thn periptwsh dhlwsewn klasewn 
				if ( !ar.m.containsKey(result) ){
					System.out.println("error: Undefined symbol '"+result+"'");
					throw new TypeCheckException();
				}
				return result;
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		String ident_type = ar.typeOfIdentifier(result);
		if ( ident_type == null || ident_type.equals("not found") ) throw new TypeCheckException();
		return ident_type;
	}

	public String visit(ThisExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		//return "this";
		try{
			if ( ar.m.get(ar.curr_class).type.equals("Main class") ){
				System.out.println("error: Unknown symbol 'this' in main class");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return ar.curr_class;
	}

	public String visit(ArrayAllocationExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// new int [ exp ]
		try{
			String exp = n.f3.accept(this, ar); //Expression
			if( !exp.equals("int") ){
				System.out.println("error: Expected expression of int type in array allocation new int['exp']");
				throw new TypeCheckException();
			}
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return "int[]";
		
	}

	public String visit(AllocationExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// new Identifier
		String result = null;
		try{
			result = n.f1.accept(this, ar); //Identifier
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}

	public String visit(BracketExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// ( exp )
		String exp = null;
		try{
			exp = n.f1.accept(this, ar); //Expression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return exp;
	}

	public String visit(NodeToken n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return n.toString();
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}


	/********   TYPES   ********/

	public String visit(Type n, ArgumentsForVisit ar) throws TypeCheckException {
		String ident = null;
		try{
			ident = n.f0.accept(this, ar); //Identifier
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return ident;
	}

	public String visit(ArrayType n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "int[]";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(BooleanType n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "boolean";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(IntegerType n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "int";	
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

}