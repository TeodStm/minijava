import java.util.*;
import java.io.*;
class ArgumentsForVisit {
	LinkedHashMap<String,ClassMapNode> m;
	String curr_class;
	String curr_method;
	Stack<ArrayList<String>> arg_list; //stoiva listwn , xrhsimopoieitai gia ton elegxo twn orismatwn kata thn klhsh methodwn
	BufferedWriter writer;
	Integer register_count;


	public ArgumentsForVisit(){
		this.m = new LinkedHashMap<String,ClassMapNode>();
		this.arg_list = new Stack<ArrayList<String>>();
		//this.arg_list = new ArrayList<String>();
		this.curr_class = " ";
		this.curr_method = " ";
		this.writer = null;
		this.register_count = 0;
	}

	public void clearStrings(){
		this.curr_class = " ";
		this.curr_method = " ";
	}

	public void initWriter(String file_name){
		try{
			this.writer = new BufferedWriter(new FileWriter(file_name));

		}
		catch(Exception e){
			System.out.println("Initialization of writer failed!");
		}
	}

	public void closeWriter(){
		try{
			this.writer.close();
		}
		catch(Exception e){
			System.out.println("Closing writer failed!");
		}
	}

	public void emit(String content){
		try{
			this.writer.write(content);
		}
		catch(Exception e){
			System.out.println("Emiting on file failed!");
		}
	}

	public void default_emit(){
		String default_content = "\n\ndeclare i8* @calloc(i32, i32)\ndeclare i32 @printf(i8*, ...)\ndeclare void @exit(i32)\n\n@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\ndefine void @print_int(i32 %i) {\n\t%_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n\n";
		try{
			this.writer.write(default_content);
		}
		catch(Exception e){
			System.out.println("Writing at output file failed!");
		}
	}

	/***** APO EDW KAI KATW EINAI THS 2hs ERGASIAS *****/
	public String typeOfIdentifier(String ident){
		if ( this.m.containsKey(ident) && !this.m.get(ident).type.equals("Main class") ) return ident;

		if ( this.m.get(this.curr_class).type.equals("Main class") ){ //Main class

			if ( this.m.get(this.curr_class).fieldMap.containsKey(ident) ){ //uparxei to kleidi sto fieldMap
				return this.m.get(this.curr_class).fieldMap.get(ident);
			}
			else if ( this.m.containsKey(ident) ) {
				return ident;
			}
			else{
				System.out.println("error: Unknown symbol '"+ident+"'");
				return "not found";
			}
		}
		else{ //se allh klash (prepei na dw kai stis super classes)
			if ( this.m.get(this.curr_class).methodMap.get(this.curr_method).args_map.containsKey(ident) ){
				//h metavlhth anoikei sta arguments ths methodou
				return this.m.get(this.curr_class).methodMap.get(this.curr_method).args_map.get(ident);
			}
			else if ( this.m.get(this.curr_class).methodMap.get(this.curr_method).local_vars_map.containsKey(ident)){
				//H metavlhth anoikei stis topikes metavlhtes ths methodou
				return this.m.get(this.curr_class).methodMap.get(this.curr_method).local_vars_map.get(ident);
			}
			else if ( this.m.get(this.curr_class).fieldMap.containsKey(ident) ){
				//H metavlhth anoikei sta fields ths klashs
				return this.m.get(this.curr_class).fieldMap.get(ident);
			}
			else{

				String result = this.typeOfIdentifierOnParent(ident, this.m.get(this.curr_class).parent_class );
				if( result.equals("not found") ){
					System.out.println("error: Unknown symbol '"+ident+"'");
					return "not found";
				}
				return result;
				
			}
		}
	}

	public String typeOfIdentifierOnParent(String ident, String parent){

		if ( parent == null ) return "not found"; //den uparxei goneas

		if ( this.m.get(parent).fieldMap.containsKey(ident) ){ //h metavlhth uparxei se gonea
			return this.m.get(parent).fieldMap.get(ident); //epistrofh tupou
		}
		else{ //den uparxei oute sthn gonikh klash
			if ( this.m.get(parent).parent_class == null ) return "not found"; //den uparxei pateras tou patera
			return this.typeOfIdentifierOnParent( ident, this.m.get(parent).parent_class );
		}

	}


	public String objectOfMethod(String class_name, String ident){

			if ( this.m.containsKey(class_name) ){ //exei oristei h klash
				if ( this.m.get(class_name).methodMap.containsKey(ident) ){ //einai methodos ths klashs
					return class_name;
				}
				else if ( this.m.get(class_name).parent_class != null ){
					//elegxos an einai methodos ths gonikhs klashs
					return this.objectOfMethod(this.m.get(class_name).parent_class, ident );
				}
				else return "not found";

			}
			else return "not found";


	}


	public String returnTupeOfMethod(String class_name, String ident){
		//epistregei ton tupo epistrofhs ths methodou 'ident'

		if ( this.m.containsKey(class_name) ){ //exei oristei h klash
			if ( this.m.get(class_name).methodMap.containsKey(ident) ){ //einai methodos ths klashs
				 return this.m.get(class_name).methodMap.get(ident).return_type;
			}
			else if ( this.m.get(class_name).parent_class != null ){ //elegxos an einai methodos gonikhs klashs
				return this.returnTupeOfMethod(this.m.get(class_name).parent_class, ident);
			}
			else return "not found";
		}
		else return "not found";

	}

	public Boolean methodDefinedOnParentClass(String class_name, String parent_class_name, String method_name ){

		if ( this.m.get(parent_class_name).methodMap.containsKey(method_name) ){
			//Uparxei methodos me idio onoma sthn gonikh klash
		
			//Return type check
			//o tupos epistrofhs ths methodou einai diaforetikos apo authn ths gonikhs klashs
			if ( !this.m.get(class_name).methodMap.get(method_name).return_type.equals(this.m.get(parent_class_name).methodMap.get(method_name).return_type) ) return true;


			//Arguments check
			//Diaforetiko plhthos orismatwn
			if ( this.m.get(class_name).methodMap.get(method_name).args_map.size() != this.m.get(parent_class_name).methodMap.get(method_name).args_map.size() ) return true;

			//Elegxos gia tou s tupous twn orismatwn
			Collection<String> coll1 = this.m.get(class_name).methodMap.get(method_name).args_map.values();
			Collection<String> coll2 = this.m.get(parent_class_name).methodMap.get(method_name).args_map.values();

			Iterator iter1 = coll1.iterator();
			Iterator iter2 = coll2.iterator();

			while( iter1.hasNext() && iter2.hasNext() ){
				String val1 = (String)iter1.next();
				String val2 = (String)iter2.next();

				if ( !val1.equals(val2) ){
					return true;
				}
			}
		}
		//Check for all parents
		if ( this.m.get(parent_class_name).parent_class != null ){ //uparxei gonikh klash ths gonikhs
			return this.methodDefinedOnParentClass(class_name, this.m.get(parent_class_name).parent_class, method_name);
		}
		return false;

	}


	public Boolean inheritanceAssignment(String type1, String type2){
		// exoume to assignment 'type1 = type2'
		//einai kapoios apo tous vasikous tupous
		if ( type1.equals("int") || type1.equals("int[]") || type1.equals("boolean") || type2.equals("int") || type2.equals("int[]") || type2.equals("boolean") ) return false;

		if ( this.m.containsKey(type1) && this.m.containsKey(type2) ){ //exei oristei h klash type2
			if ( this.m.get(type2).parent_class != null ){ //exei gonikh klash
				if ( this.m.get(type2).parent_class.equals(type1) ) return true; //type2 extends type1
				else return this.inheritanceAssignment(type1, this.m.get(type2).parent_class );
			}
			else return false;
		}
		else return false;
	}


	public Boolean validArguments(String class_name, String method_name){
		if ( this.m.containsKey(class_name) ){ //elegxos an uparxei h klash
			if ( this.m.get(class_name).methodMap.containsKey(method_name) ){ //elegxos an uparxei h methodos
				Collection<String> coll = this.m.get(class_name).methodMap.get(method_name).args_map.values();
				if ( coll.size() != this.arg_list.peek().size() ){ //diaforetiko plhthos orismatwn
					System.out.println("error: Method '"+method_name+"' expected "+coll.size()+" arguments but "+this.arg_list.peek().size()+" given");
					return false;
				}
				Iterator coll_iter = coll.iterator();
				Iterator list_iter = this.arg_list.peek().iterator();
				int i = 1;
				while( coll_iter.hasNext() && list_iter.hasNext() ){
					String coll_argument = (String)coll_iter.next();
					String list_argument = (String)list_iter.next();

					if ( !coll_argument.equals(list_argument) ){
						if ( !this.existsInheritance(coll_argument, list_argument) ){
							System.out.println("error: Method '"+method_name+"' expected "+coll_argument+" but "+list_argument+" given on argument "+i);
							return false;
						}
					}
					i++;
				}
				return true;
			}
			else return false;
		}
		else return false;
	}

	public Boolean existsInheritance(String parent, String child){
		if ( !this.m.containsKey(parent) || !this.m.containsKey(child) ) return false; //elegxos an oi klaseis uparxoun
		
		if ( this.m.get(child).parent_class != null ){ //uparxei gonikh klash ths child
			if ( this.m.get(child).parent_class.equals(parent) ) return true; //h parnt einai gonikh ths child
			else return this.existsInheritance(parent, this.m.get(child).parent_class ); //elegxos klhronomikothtas megaluterou vathmou
		}
		else return false;
		
	}


	public Boolean compatibleWithParentClass(String type, String field_name, String parent){
		if ( this.m.containsKey(parent) && this.m.containsKey(this.curr_class) && !this.m.get(this.curr_class).type.equals("Main class") ){ //h klash uparxei kai den einai h main 
			if ( this.m.get(parent).fieldMap.containsKey(field_name) ){ //uparxei pedio me idio onoma sth gonikh klash
				if ( this.m.get(parent).fieldMap.get(field_name).equals(this.m.get(this.curr_class).fieldMap.get(field_name)) ){
					//an h metavlhth uparxei me idio onoma kai tupo sthn gonikh klash (error)
					return false;
				}
			}
			
			if ( this.m.get(parent).parent_class != null ) return compatibleWithParentClass(type, field_name, this.m.get(parent).parent_class );
			else return true;
		}
		return false;
	}

	/***** OFFSETS *****/

	public int findLastFieldOffset(String class_name){
		if ( this.m.get(class_name).parent_class == null ) return 0; //den uparxei gonikh klash

		String parent = this.m.get(class_name).parent_class;
		int offset = 0;
		for( String i : this.m.get(parent).offsetFieldMap.keySet() ){ 
			//String field_type = this.m.get(parent).offsetFieldMap.get(i);
			String field_type = this.m.get(parent).fieldMap.get(i);
			/*if ( field_type == null){
				System.out.println("1ERROR at offsets");
				return -1;
			}*/

			if ( field_type.equals("int") ) offset = 4;
			else if ( field_type.equals("boolean") ) offset = 1;
			else offset = 8;
			offset = offset + this.m.get(parent).offsetFieldMap.get(i);
		}
		return offset;
		//to offset periexei to offset tou teleutaiou pediou ths parent klashs + to 
		//megethos tou teleutaiou pediou

	}

	public int findLastMethodOffset(String class_name){
		if ( this.m.get(class_name).parent_class == null ) return 0; //den uparxei parent class

		int offset = 0;
		int method_size = 8;
		String parent = this.m.get(class_name).parent_class;
		for( String i : this.m.get(parent).offsetMethodMap.keySet() ) offset = method_size + this.m.get(parent).offsetMethodMap.get(i);
		return offset;
	}

	public void outputOffsets(){
		System.out.println("Classes");

		for(String i : this.m.keySet()){ //gia kathe klash tou arxeiou
			if ( !this.m.get(i).type.equals("Main class") ){ //ektos apo thn main klash
				this.initOffsetFieldMap(i);   //arxikopoihsh twn offsetMaps
				this.initOffsetMethodMap(i);
			}
		}

		int field_ofst = 0;
		int method_ofst = 0;

		for(String i : this.m.keySet()){ //gia kathe klash tou arxeiou
			//field_ofst = 0;
			field_ofst = this.findLastFieldOffset(i);
			
			if ( !this.m.get(i).type.equals("Main class") ){ //ektos apo thn main klash
				for(String j : this.m.get(i).offsetFieldMap.keySet()){ //gia kathe pedio
					String fld_type = this.m.get(i).fieldMap.get(j); //typos tou pediou
					if ( field_ofst == 0 ){ //prwto pedio
						/*if(fld_type == null){
							System.out.println("ERROR at offsets");
							System.exit(0);
						}*/
						if( fld_type.equals("int") ){ //int type
							field_ofst += 4;

						}
						else if( fld_type.equals("boolean") ){ //boolean type
							field_ofst++;
						}
						else{ //pointer
							field_ofst += 8;
						}
						continue;
					}
					this.m.get(i).offsetFieldMap.put(j, field_ofst);
						/*if(fld_type == null){
							System.out.println("ERROR at offsets");
							System.exit(0);
						}*/
						if( fld_type.equals("int") ){ //int type
							field_ofst += 4;

						}
						else if( fld_type.equals("boolean") ){ //boolean type
							field_ofst++;
						}
						else{ //pointer
							field_ofst += 8;
						}

				}

				//methodoi
				method_ofst = this.findLastMethodOffset(i);
				for(String j : this.m.get(i).offsetMethodMap.keySet()){ //gia kathe pedio
					this.m.get(i).offsetMethodMap.put(j, method_ofst);
					method_ofst += 8;
				}
				
			}
		}
		//ektupwsh
		for(String i : this.m.keySet()){ //gia kathe klash tou arxeiou
			if ( !this.m.get(i).type.equals("Main class") ){ //ektos apo thn main klash
				System.out.println("\t"+i );
				for(String j : this.m.get(i).offsetFieldMap.keySet()){
					System.out.println("\t\t"+i+"."+j+" : "+this.m.get(i).offsetFieldMap.get(j));
				}
				for(String j : this.m.get(i).offsetMethodMap.keySet()){
					System.out.println("\t\t"+i+"."+j+" : "+this.m.get(i).offsetMethodMap.get(j));
				}
			}
		}
	}

	public void initOffsetFieldMap(String class_name){
        if ( !this.m.get(class_name).fieldMap.isEmpty() ){ //an to map twn pediwn ths klashs den einai adeio
            Collection<String> coll = this.m.get(class_name).fieldMap.keySet();
            Iterator iter = coll.iterator();

            while( iter.hasNext() ){
                String fieldKey = (String)iter.next();
                this.m.get(class_name).offsetFieldMap.put(fieldKey, 0);
            }
        }
    }

    public void initOffsetMethodMap(String class_name){
        if ( !this.m.get(class_name).methodMap.isEmpty() ){ //an to map twn methodwn ths klashs den einai adeio
            Collection<String> coll = this.m.get(class_name).methodMap.keySet();
            Iterator iter = coll.iterator();

            while( iter.hasNext() ){
                String methodKey = (String)iter.next();
                if ( !this.methodDefinedInParent(class_name, methodKey) ) this.m.get(class_name).offsetMethodMap.put(methodKey, 0);
            }

        }
        
    }

    public Boolean methodDefinedInParent(String class_name, String method_name){
    	if ( this.m.get(class_name).parent_class == null ) return false;

    	String parent = this.m.get(class_name).parent_class;
    	if ( this.m.get(parent).methodMap.containsKey(method_name) ) return true;
    	else return this.methodDefinedInParent(parent, method_name);
    }

}