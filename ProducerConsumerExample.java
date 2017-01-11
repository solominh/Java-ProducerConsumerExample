import java.util.ArrayList;

public class ProducerConsumerExample {

    public static void main(String[] args) {
        MyBlockQueue<String> myBlockQueue = new MyBlockQueue<String>(4);

        int producerCount = 4;
        for (int i = 0; i < producerCount; i++) {
            String name = "Producer " + Integer.toString(i);
            Producer producer = new Producer(myBlockQueue, name);
            producer.start();
        }

        int customerCount = 20;
        for (int i = 0; i < customerCount; i++) {
            String name = "Customer " + Integer.toString(i);
            Customer customer = new Customer(myBlockQueue, name);
            customer.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
            }
        }

    }

    public static class MyBlockQueue<E> {
        private int maxSize;
        private final Object lock = new Object();
        private final ArrayList<E> queue = new ArrayList<E>();

        public MyBlockQueue(int maxSize) {
            this.maxSize = maxSize;
        }

        public boolean produce(String producerName, E e) {
            synchronized (lock) {
                System.out.println(producerName + " is waiting to serve " + e);
                while (queue.size() >= this.maxSize) {
                    try {
                        lock.wait();
                    } catch (InterruptedException exception) {
                    }
                }

                boolean success = this.queue.add(e);
                System.out.println(producerName + " is serving " + e);
                lock.notifyAll();
                return success;
            }
        }

        public E consume(String consumerName) {
            synchronized (lock) {
                System.out.println(consumerName + " is waiting to be served");
                while (queue.size() <= 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException exception) {
                    }

                }
                E e = this.queue.remove(0);
                System.out.println(consumerName + " is being served " + e);
                lock.notifyAll();
                return e;
            }
        }

    }

    public static class Producer extends Thread {

        private MyBlockQueue<String> myBlockQueue;
        private String name;
        private int productCount;

        public Producer(MyBlockQueue<String> myBlockQueue, String name) {
            this.myBlockQueue = myBlockQueue;
            this.name = name;
            this.productCount = 0;
        }

        @Override
        public void run() {
            int numberOfProducts = 7;
            for (int i = 0; i < numberOfProducts; i++) {
                productCount++;
                String productName = name + "_Product number " + Integer.toString(productCount);
                this.myBlockQueue.produce(this.name, productName);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException exception) {
                }

            }
        }

    }

    public static class Customer extends Thread {

        private MyBlockQueue<String> myBlockQueue;
        private String name;

        public Customer(MyBlockQueue<String> myBlockQueue, String name) {
            this.myBlockQueue = myBlockQueue;
            this.name = name;
        }

        @Override
        public void run() {
            String product = this.myBlockQueue.consume(this.name);
        }
    }
}
