
public class AddOns {
	
    	int id;
	    String AddOnName;
	    double AddOnPrice;
	    boolean Availability;


	    public  AddOns(  int id,String AddOnName,double AddOnPrice,boolean Availability) {
	        this.id = id;
	        this.AddOnName = AddOnName;
	        this.AddOnPrice = AddOnPrice;
	        this.Availability = Availability;
	  
	    }
	    
	    public int id(){
	    	return id;
	    	
	    }
	    
	    public String AddOnName(){
	    	return AddOnName;
	    	
	    }
	    public double AddOnPrice(){
	    	return AddOnPrice;
	    	
	    }
	    public boolean Availability(){
	    	return Availability;
	    	
	    }

	

}
