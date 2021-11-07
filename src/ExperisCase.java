/*
*      @author: David Thomsen 
*               davidThomsen216@gmail.com
*               https://github.com/DaThGIT/experisCase code source
*
*      Movie suggestion program, written as a test-case during a recruting process with Experis Academy.
*      Basically reads in a database of users and products as per a pre specified .txt format(see movie_product_data folder).
*           
*           - Problem 1: 
*                            Consists of creating a general list of recommendations.
*                            The program achieves this by computing a list of the highest rated movie in each genre,
*                            then picking out the movies that has a product purchase rate(sum of purchases across the userbase)
*                            larger than 0 (movies which have been bought more than one time). These choices are then printed as recommendations. 
*           - Problem 2: 
*                            Consists of giving movie recommendations based on a single viewed it from the product database.
*                            The idea is here, that a currentUserSession file is read, and from that the program should recommend movies
*                            that relate to that single movie a single user is viewing. 
*                            The program achieves this by computing, for each user in the currentUserSession file, a list of movies
*                            that the user currently has not viewed or purchased, and share a single shared genre with the movie currently being viewed.
*                            The highest rated movie in each shared genre is computed, and these choices are printed as recommendations.   
*/              
import java.io.*;
import java.util.*;

class ExperisCase {
        // Running the code, calling the functions, testing the data, solving the given problems.     
        public static void main(String[] args) {
            try {
                    solveProblems();
            } catch (IOException e) {
                System.out.println("oops something went wrong\n" + e + " ruined the program :///");
            }
                
        }

    // Solution    
    private static void solveProblems() throws IOException {
                        List<User> users = readUsers();                             // data structure containing all user objects
        //              printUsers(users);                                          // test-print users
                        List<Product> products = readProducts();                    // data structure containing all product objects
        //              printProducts(products);                                    // test-print products
                        System.out.println("\nProblem 1 Solution: \n");
                        printPopularProducts(popList(products, users));             // solve and print solution to problem 1 
                        System.out.println("\nProblem 2 Solution: \n");
                        printUserRec(collectUserRec(users, products));              // solve and print solution to problem 2
    }    

    // User suggestions:
    // popular products:
    private static Set<String> popList(List<Product> products, List<User> users){
            Set<String> res = new HashSet<>();
            List<Product> genreHRP = genreHRPpopList(products);
            String st;
                for (Product HRP : genreHRP) {
                    if (productPurchaseRate(HRP.getId(), users) > 0){
                        st = "Name: " + HRP.getOGName() + "\nRating: " + HRP.getRating() + "\nPrice: " + HRP.getPrice();
                        res.add(st);         
                    }    
                }
            return res;    
    }
    private static List<Product> genreHRPpopList(List<Product> products){
            List<Product> res = new LinkedList<>(); 
            Set<String> genres = getCategorySet(products);
                for (String string : genres) {
                    List<Product> movies = new ArrayList<>(); 
                    for (Product product : products) {
                        if (product.getKeywords().contains(string)) movies.add(product); 
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
                     if (id == itemId) res++;
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
    private static Map<User, List<String>> collectUserRec(List<User> users, List<Product> products) throws IOException {
            Map<User, List<String>> res = new HashMap<>();
            Map<User, Integer> userRequests = currentUserRequests(users);
                for (Map.Entry<User, Integer> entry : userRequests.entrySet()) {
                     res.put(entry.getKey(), userRecList(handleRequest(entry.getKey(), entry.getValue(), products, users)));                     
                }
            return res;
    }
    private static List<String> userRecList(Set<Product> products){
            List<String> res = new LinkedList<>();
            String st;
                for (Product product : products) {
                    st = "Name: " + product.getOGName() + "\nRating: " + product.getRating() + "\nPrice: " + product.getPrice();
                    res.add(st);
                }
            return res;
    } 
    private static Set<Product> handleRequest(User user, int id, List<Product> products, List<User> users){
            Set<Product> res = new HashSet<>();
            Set<String> keywords = new HashSet<>();
                for (Product product : products) {
                    if (id == product.getId()) keywords = product.getKeywords();
                }
            List<Product> relatedProducts = new LinkedList<>();    
                for (String string : keywords) {
                    for (Product product : products) {
                         if (((product.getKeywords().contains(string)) && !(user.getViewed().contains(product.getId())))
                               && !(user.getPurchased().contains(product.getId()))) relatedProducts.add(product);    
                    }
                }
            res.addAll(genreHRPUserRec(relatedProducts, keywords)); 
            return res;         
    }
    private static List<Product> genreHRPUserRec(List<Product> products, Set<String> keywords){
            List<Product> res = new LinkedList<>();
                for (String string : keywords) {
                    List<Product> movies = new LinkedList<>();
                    for (Product product : products) {
                        if (product.getKeywords().contains(string)) movies.add(product);
                    }
                    res.add(highestRatedProduct(movies));
                }
            return res; 
    }
    private static Map<User, Integer> currentUserRequests(List<User> users) throws IOException {
            Map<User, Integer> res = new HashMap<>();
            File file = new File("C:\\Users\\david\\Desktop\\experisCase\\experisCase\\Movie_product_data\\CurrentUserSession.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
                while ((st = br.readLine()) != null) {
                    String[] currentUser = removeWS(st).split(",");
                        for (User user: users) {
                             if (Integer.parseInt(currentUser[0]) == user.getId()) res.put(user, Integer.parseInt(currentUser[1]));
                        }
                }      
            br.close();
            return res;
    }

    // Data reading:  
    // Products     
    private static List<Product> readProducts() throws IOException {
            File file = new File("C:\\Users\\david\\Desktop\\experisCase\\experisCase\\Movie_product_data\\Products.txt");
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
                    if (productData[i].length() > 0){
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
            File file = new File("C:\\Users\\david\\Desktop\\experisCase\\experisCase\\Movie_product_data\\Users.txt");
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
    private static void printUserRec(Map<User, List<String>> userRec){
        for (Map.Entry<User, List<String>> entry : userRec.entrySet()) {
            System.out.println("\n");
            System.out.println("Username: " + entry.getKey().getName() + " || user id " + entry.getKey().getId() + "\n");
            System.out.println("recommendations: ");
                for (String recommendation : entry.getValue()) {
                    System.out.print("\n" + recommendation + "\n");
                }
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