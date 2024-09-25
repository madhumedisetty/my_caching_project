package com.example.BankingSystem;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
public class BankingSystem2 {
    private final Map<Integer, BankAccount> cache=new ConcurrentHashMap<>();
    private final Map<Integer,BankAccount>  dataBase=new HashMap<>();
    private final int cache_Size=5;
    private final AtomicInteger cache_hits=new AtomicInteger(0);
    private final AtomicInteger cache_Misses=new AtomicInteger(0);
    private static class BankAccount{
        private int acc_no;
        private String name;
        private double balance;
        BankAccount(int acc_no, String name, double balance){
            this.acc_no=acc_no;
            this.name=name;
            this.balance=balance;
        }
        @Override
        public String toString(){
            return "bank{acc_no:"+acc_no+",name:"+name+",balance :"+balance+"}";
        }
        public void withDraw(int amt){
            if(balance>=amt){
                System.out.println("With Drawal done with ammount"+amt);
                balance-=amt;
                System.out.println("Remaining balance:"+balance);
            }
            else{
                System.out.println("Insufficient Funds");
            }
        }
        public void Deposit(int amt){
            System.out.println("before deposit the balance is:"+balance);
            balance+=amt;
            System.out.println("After deposit the balance is:"+balance);
        }
    }
    public BankingSystem2(){
        dataBase.put(1,new BankAccount(1,"Vishnu",24000));
        dataBase.put(2,new BankAccount(2,"Udaya",23450));
        dataBase.put(3,new BankAccount(3,"Lalitha",23540));
        dataBase.put(4,new BankAccount(4,"Madhuri",42350));
        dataBase.put(5,new BankAccount(5, "Anushka", 54320));
    }
    public BankAccount getAccount(int id){
        BankAccount bankCache=cache.get(id);
        if(bankCache!=null){
            cache_hits.incrementAndGet();
            return bankCache;
        }
        else{
            cache_Misses.incrementAndGet();
            BankAccount bankDataBase=dataBase.get(id);
            if(bankDataBase!=null){
                addToCache(id,bankDataBase);
                return bankDataBase;
            }
        }
        return null;
    }
    public void addToCache(int id,BankAccount bank){
        if(cache.size()>=cache_Size){
            int key_To_remove=cache.keySet().iterator().next();
            cache.remove(key_To_remove);
        }
        cache.put(id,bank);
    }
    public void cacheStatistics(){
        System.out.println("cache_hits:"+cache_hits.get());
        System.out.println("Cache Misses:"+cache_Misses.get());
        System.out.println("Cache hit ratio is:"+(double)cache_hits.get()/(cache_Misses.get()+cache_hits.get()));
        System.out.println("Accounts in cache"+cache.keySet());
    }
 public static void main(String[] args){
    Scanner sc=new Scanner(System.in);
    BankingSystem2 bank=new BankingSystem2();
    ExecutorService executor=Executors.newFixedThreadPool(2);
    executor.execute(()->{
    for(int i=0;i<10;i++){
        boolean input=true;
        System.out.println("type the account_id");
        int accId=sc.nextInt();
        BankAccount ba=bank.getAccount(accId);
        if(ba!=null){
            System.out.println("Bank account was found:"+ba);
            while(input){
            System.out.println("Enter the operation u want to perform");
            String action=sc.next();
            System.out.println("Enter the amount u need to perform for a withdraw or deposit");
            int amt=sc.nextInt();
            switch(action){
                case "withdraw":ba.withDraw(amt);
                break;
                case "deposit":ba.Deposit(amt);
                break;
                default:System.out.println("Given actions r not performed");
            }
            System.out.println("if u want to perform the more operations on this account(y/n):");
            char ch=sc.next().charAt(0);
            if(ch!='y'){
                input=false;
            }
          }
        }
        else{
            System.out.println("You have entered the wrong id :"+ba);
        }
    }
  });
  try{
    Thread.sleep(1000000);
  }catch(Exception e){
    e.printStackTrace();
  }
    //sc.close();
    bank.cacheStatistics();
 }
}