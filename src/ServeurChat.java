import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread{
    int nbClients;
    // creation d'une liste pour sauvgareder les clients connectés
    private List <Socket> clientConnectes = new ArrayList<>();

    public void run(){
        try {
          ServerSocket ss = new ServerSocket(234);
          // notre serveur est capable de connecté des client a n'importe quel moment
            while (true){
                // generation dune socket
                Socket s = ss.accept();
                //chaque client cnx je vais l'ajouter a la liste
                clientConnectes.add(s);
                ++nbClients;
                //Conversation c=new Conversation(s, nbClients);
                //clientConnectes.add(c);
                //c.start();
                // pour chaque client connecte ont generent un thread
                new Conversation(s, nbClients).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creation d'une methode pour deffuser le msg d'un client au autre
    public void BroadCast(String message){
        for (Socket s:clientConnectes) {
            try {
                PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                pw.println(message);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // pour chaque objet qui se connecte on cree un objet de la classe Conversation ( qui est un nouveau thread )
    // le thread cree va executé la methode run
    class Conversation extends Thread{
        public Socket socket;
        public int numeroClient;

        public Conversation(Socket socket, int num){
            super();
            this.socket = socket;
            this.numeroClient = num;
        }
        // code executer d'une maniere parallele
        // code de la conversation
        public void run(){
            //des la connexion du client ce code est execute
            try {

                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);

                //les detailles du client ( @ip )
                String IP = socket.getRemoteSocketAddress().toString();
                System.out.println("Connextion du client : " + numeroClient + " avec l'adresseIP :" + IP);
                pw.println("Bienvenue , vous etes le client numero " + numeroClient);

                while (true){
                    String req;
                    while ((req = br.readLine())!= null){
                        System.out.println("Le client : " + IP + "a envoyer :"+ req);
                        BroadCast(req);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){

        new ServeurChat().start();
    }

}
