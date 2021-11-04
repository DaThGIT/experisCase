import java.io.*;
import java.util.*;

class ExperisCase {
        // Running the code, calling the functions, testing the data.     
        public static void main(String[] args) {
            try {
                List<User> users = readUsers();                             // data structure containing all user objects
//              printUsers(users);                                          // test print users
                List<Product> products = readProducts();                    // data structure containing all product objects
//              printProducts(products);                                    // test print products
                Set<String> popularProducts = popList(products, users);     // solve problem 1
                printPopularProducts(popularProducts);                      // print solution to problem 1            
            } catch (Exception e) {
                System.out.println("error " + e + " occurred");
            }           
        }



    // User suggestions:
    // popular products:
    private static Set<String> popList(List<Product> products, List<User> users){
        Set<String> res = new HashSet<>();
        List<Product> genreHRP = genreHRP(products);
        String st;
            for (Product HRP : genreHRP) {
                if (productPurchaseRate(HRP.getId(), users) > 0){
                    st = "Name: " + HRP.getOGName() + "\nRating: " + HRP.getRating() + "\nPrice: " + HRP.getPrice();
                    res.add(st);         
                }    
            }
        return res;    
    }
    private static List<Product> genreHRP(List<Product> products){
       List<Product> res = new LinkedList<>(); 
       Set<String> genres = getCategorySet(products);
       for (String string : genres) {
            List<Product> movies = new ArrayList<>(); 
            for (Product product : products) {
                    if(product.getKeywords().contains(string)) movies.add(product); 
            }
            res.add(highestRatedProduct(movies));           
       }
       return res;
    }
    private static Product highestRatedProduct(List<Product> products){
            Product res = products.get(0);
                for (Product product : products) {
                    if (product.getRating() > res.getRating()) res = product;
                }
            return res;
    }
    private static int productPurchaseRate(int itemId, List<User> users){
        int res = 0;
            for (User user : users) {
                for (int id : user.getPurchased()) {
                     if(id == itemId) res++;
                }
            } 
        return res;
    }
    private static Set<String> getCategorySet(List<Product> products){
            Set<String> res = new HashSet<>();
                for (Product product : products) {
                    res.addAll(product.getKeywords());
                } 
            return res; 
    }

    // Recommended products:





    // Data reading:  
    // Products     
    private static List<Product> readProducts() throws IOException {
            File file = new File("C:\\Users\\david\\Desktop\\experisCase\\Movie_product_data\\Products.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            List<Product> products = new LinkedList<>();
                while ((st = br.readLine()) != null) {
                    String OGname;
                    String[] arr = st.split(",");
                    OGname = arr[1];
                    String[] productData = removeWS(st).split(",");  
                    Product product = createProduct(productData, OGname);
                    products.add(product);    
                }
            br.close();
            return products;
    }
    private static Product createProduct(String[] productData, String OGname){
            Set<String> keywords = new HashSet<>();
            for (int i = 3; i < 8; i++) {
                if(productData[i].length() > 0){
                    String st = productData[i];
                    keywords.add(st);
                } 
            }
            Product product = new Product(Integer.parseInt(productData[0]), OGname, productData[1], Integer.parseInt(productData[2]),
                                          Double.parseDouble(productData[8]), Integer.parseInt(productData[9]), keywords);
            return product;
    }
    
    // Users
    private static List<User> readUsers() throws IOException {
            File file = new File("C:\\Users\\david\\Desktop\\experisCase\\Movie_product_data\\Users.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            List<User> users = new LinkedList<>();
                while ((st = br.readLine()) != null) {
                    String[] userData = removeWS(st).split(",");
                    User user = createUser(userData);
                    users.add(user);
                }
            br.close();
            return users;
    }   
    private static User createUser(String[] userData){     
        User user = new User(Integer.parseInt(userData[0]), userData[1], parseToIntSet(userData[2].split(";")),
                             parseToIntSet(userData[3].split(";")));
        return user;
    }
    
    // helper methods for cleaning/parsing data
    private static Set<Integer> parseToIntSet(String[] data){
        Set<Integer> res = new HashSet<>();  
        for (int i = 0; i < data.length; i++) {
                res.add(Integer.parseInt(data[i]));
        }
        return res;
    }
    private static String removeWS(String st){
        return st.replaceAll("\\s+","");
    }

    // Print methods: 
    private static void printProducts(List<Product> products){
        System.out.println("\nProducts: ");
        for (Product product : products) {
             System.out.println();
             System.out.println("id; " + product.getId() + "\nname: " + product.getName() + "\nyear: " + product.getYear()
                                 + "\nrating: " + product.getRating() + "\nprice: " + product.getPrice() + "\nkeywords: ");
             System.out.println();
             for (String keyword : product.getKeywords()) {
                    System.out.print(keyword + "\n");
             }
            System.out.println();   
        }    
    }
    private static void printUsers(List<User> users){
        System.out.println("\nUsers: ");    
        for (User user : users) {
             System.out.println();
             System.out.println("id: " + user.getId() + "\nname: " + user.getName() + "\nproducts viewed: ");
             for (int id: user.getViewed()) {
                 System.out.print(id + " ");                
             }
             System.out.println("\nproducts purchased");
             for (int id : user.getPurchased()) {
                 System.out.print(id + " ");
             }
             System.out.println();        
        } 
    }
    private static void printPopularProducts(Set<String> popularProducts){
        System.out.println();
        for (String string : popularProducts) {
            System.out.println(string);
            System.out.println();
        }
    }    
}

// structure of data: 
// object class for handling of user data
class User {
int id;
String name; 
Set<Integer> viewed;
Set<Integer> purchased;

    public User(int i, String n, Set<Integer> v, Set<Integer> p) {
        id = i;
        name = n;
        viewed = v;
        purchased = p;    
    }
    public int getId(){
        return id; 
    }
    public String getName(){
        return name;     
    }
    public Set<Integer> getViewed(){
        return viewed;
    }
    public Set<Integer> getPurchased(){
        return purchased;
    }
}

// object class for handling product data
class Product {
int id;
String OGname; 
String name; 
int year; 
double rating; 
int price; 
Set<String> keywords;

    public Product(int i, String On, String n, int y, double r, int p, Set<String> k){
            id = i;
            OGname = On;
            name = n;
            year = y;
            rating = r;
            price = p; 
            keywords = k;
    }
    public int getId(){
        return id;         
    }
    public String getOGName(){
        return OGname;
    }
    public String getName(){
        return name;        
    }    
    public int getYear(){
        return year;
    }
    public double getRating(){
        return rating;
    }
    public int getPrice(){
        return price;
    }
    public Set<String> getKeywords(){
        return keywords;
    }
}