package route;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Route 
{
    public static void main(String[] args) throws FileNotFoundException 
    {
        File inputFile1 = new File("airports.txt");
        Scanner input1 = new Scanner(inputFile1);
        int counter1 = 0;
        int vertexCount = Integer.parseInt(input1.nextLine());
        HashMap hashMap = new HashMap(1000);
        String[] airportCodes = new String[vertexCount - 1];
        
        while(input1.hasNextLine() && counter1 < vertexCount)
        {
            Vertex vertex = new Vertex(input1.nextLine());
            airportCodes[counter1] = vertex.airport_code;
            hashMap.insert(vertex.airport_code , vertex);
            counter1++;
        }
        input1.close();
        
        
        File inputFile2 = new File("flights.txt");
        Scanner input2 = new Scanner(inputFile2);
        int counter2 = 0;
        int edgeCount = Integer.parseInt(input2.nextLine());
        input2.nextLine();
        
        while(input2.hasNextLine() && counter2 < edgeCount)
        {
            String edgeInfo[] = input2.nextLine().split("\\s+");
            Edge edge = new Edge(edgeInfo[0] , Integer.parseInt(edgeInfo[1]) , edgeInfo[2] , edgeInfo[3] , Integer.parseInt(edgeInfo[4]) , Integer.parseInt(edgeInfo[5]));
            linkedList adjList = hashMap.get(edgeInfo[2]).vertex.adj_list;
            adjList.insert(adjList , edge);
            counter2++;
        }
        input2.close();
        
        
        if(args.length > 0)
        {
            String sourceAir = args[0];
            String destAir = args[1];
            
            dijkAlgo(hashMap , hashMap.get(sourceAir).vertex);
            Vertex pointer = hashMap.get(sourceAir).vertex;
            String shortestPath = sourceAir;
            shortestPath += "-";
            
            while(pointer.parent != null && pointer != hashMap.get(destAir).vertex)
            {
                shortestPath += pointer.parent;
                shortestPath += "-";
                pointer = hashMap.get(pointer.parent).vertex;
            }
            shortestPath += hashMap.get(destAir).vertex.dvalue;
            
            System.out.print(shortestPath);
            System.out.println();
        }
    }
    
    private static void dijkAlgo(HashMap hashMap , Vertex source)
    {
        MinHeap minHeap = new MinHeap(1000);
        int shortestPath = 0;
        
        for(int i = 0 ; i < hashMap.maxMapSize ; i++)
        {
            if(hashMap.hashMap[i] != null)
            {
                Vertex vertex = hashMap.hashMap[i].vertex;
            
                if(vertex.equals(source))
                {
                    vertex.dvalue = 0;
                }
                else
                {
                    vertex.dvalue = Integer.MAX_VALUE;
                }
            
                minHeap.insert(vertex);
            }
        }
        
        while(minHeap.isEmpty())
        {
            Vertex vertex = minHeap.extractMin();
            
            if(vertex != null)
            {
                Edge edge = vertex.adj_list.head;
                int edgeCount = vertex.adj_list.length;
            
                for(int i = 0 ; i < edgeCount ; i++)
                {
                    if(edge.dest != null)
                    {
                        relaxEdge(hashMap , minHeap , edge);
                        Vertex newVertex = hashMap.get(edge.dest).vertex;
                        shortestPath = vertex.dvalue + edge.arrtime;
                
                        if(shortestPath < newVertex.dvalue)
                        {
                            newVertex.dvalue = shortestPath;
                            minHeap.decreaseKey(newVertex.heap_pos , newVertex);
                        }
                        edge = edge.next;
                    }
                }
            }
        }
    }
    
    private static void relaxEdge(HashMap hashMap , MinHeap minHeap , Edge edge)
    {
            Vertex vertex = hashMap.get(edge.origin).vertex;
        
            if(vertex.dvalue <= edge.deptime)
            {
                return;
            }
        
            vertex = hashMap.get(edge.dest).vertex;
        
            if(vertex.dvalue > edge.arrtime)
            {
                vertex.dvalue = edge.arrtime;
                minHeap.decreaseKey(vertex.heap_pos , vertex);
                vertex.parent = hashMap.get(edge.origin).vertex.airport_code;
                vertex.parentEdge = edge;
            }
    }
}

/******************************************************************************/

class Vertex
{
    String airport_code;
    int heap_pos;
    int hash_pos;
    linkedList adj_list;
    int dvalue;
    String parent;
    Edge parentEdge;
    
    public Vertex()
    {
        
    }
    
    public Vertex(String airportCode)
    {
        this.airport_code = airportCode;
        this.adj_list = new linkedList();
    }
    
    public void addEdge(Edge edge)
    {
        this.adj_list.insert(this.adj_list , edge);
    }
}

/******************************************************************************/

class Edge
{
    String origin;
    String dest;
    String airlines[];
    int fltno;
    int deptime;
    int arrtime;
    Edge next;
    
    Edge() 
    {
        
    }
    
    public Edge(String airline , int flightNum , String originAir , String destAir , int depTime , int arrTime)
    {
        this.fltno = flightNum;
        this.origin = originAir;
        this.dest = destAir;
        this.deptime = depTime;
        this.arrtime = arrTime;
    }
}
    
/******************************************************************************/

class HashMap
{
    public HashNode hashMap[];
    public int maxMapSize;
    private int mapSize; 
    
    public HashMap()
    {
    
    }
    
    public HashMap(int arraySize) 
    { 
        this.hashMap = new HashNode[arraySize];
        maxMapSize = arraySize; 
        mapSize = 0;
        
        for (int i = 0 ; i < maxMapSize ; i++) 
        {
            this.hashMap[i] = null;
        }
    }
    
    public void insert(String key , Vertex vertex)
    {
        int index = myHash(key.toCharArray()); 
        HashNode node = get(key);
        
        if (node != null) 
        {
            mapSize++; 
            HashNode newNode = new HashNode(key , vertex);
            node.linkedList.add(newNode);
        }
        else
        {
            mapSize++;
            node = new HashNode(key, vertex);
            set(index , node);
        }
    }
    
    public HashNode get(String key) 
    {
        int index = myHash(key.toCharArray());
        HashNode head = hashMap[index];
        
        return head;
    }
    
    public void set(int index , HashNode node)
    {
        this.hashMap[index] = node;
    }
    
    public int getMapSize() 
    { 
        return this.mapSize; 
    } 
    
    public boolean isEmpty() 
    { 
        return this.mapSize == 0; 
    }
    
    private int myHash(char c[])
    {
        int p0 = (int) c[0] - 'A' + 1;
        int p1 = (int) c[1] - 'A' + 1;
        int p2 = (int) c[2] - 'A' + 1;

        int p3 = p0*467*467 + p1*467 + p2;
        int p4 = p3%7193;

        return p4%1000;
    }
}

/******************************************************************************/

class HashNode
    { 
        String key; 
        Vertex vertex;
        LinkedList<HashNode> linkedList;
    
        public HashNode()
        {
    
        }
    
        public HashNode(String key, Vertex vertex) 
        { 
            this.key = key; 
            this.vertex = vertex; 
            this.linkedList = new LinkedList<>();
        }
    }

/******************************************************************************/

class MinHeap
{
    private Vertex[] array; 
    private int heapSize; 
    private int maxsize;
    
    public MinHeap()
    {
        
    }
  
    public MinHeap(int maxsize) 
    { 
        this.maxsize = maxsize; 
        this.heapSize = 0; 
        array = new Vertex[this.maxsize]; 
        array[0] = null;
    }
    
    private int parent(int pos) 
    {
        return pos / 2; 
    }
    
    private int leftChild(int pos) 
    { 
        return (2 * pos); 
    }
    
    private int rightChild(int pos) 
    { 
        return (2 * pos) + 1; 
    }
    
    private boolean isLeaf(int pos) 
    { 
        return pos >= (heapSize / 2) && pos <= heapSize; 
    }
    
    public boolean isEmpty()
    {
        return this.heapSize != 0;
    }
    
    private void swap(int x, int y) 
    { 
        Vertex tmp = array[x];
        array[x] = array[y]; 
        array[y] = tmp; 
        this.array[x].heap_pos = y;
        this.array[y].heap_pos = x;
    }
    
    public void insert(Vertex element) 
    { 
        if(this.heapSize < 1)
        {
            this.array[0] = element;
            this.heapSize++;
        }
        else
        {
            this.array[this.heapSize] = element;
            this.heapSize++;
            floatUp(this.heapSize - 1);
        }
    }
    
    private void sinkDown(int pos) 
    {
        if (!isLeaf(pos) && array[leftChild(pos)] != null && array[rightChild(pos)] != null) 
        {
            if (array[pos].dvalue > array[leftChild(pos)].dvalue || array[pos].dvalue > array[rightChild(pos)].dvalue) 
            {
                if (array[leftChild(pos)].dvalue < array[rightChild(pos)].dvalue) 
                {
                    array[pos].dvalue = leftChild(pos);
                    array[leftChild(pos)].dvalue = pos;
                    swap(pos, leftChild(pos)); 
                    sinkDown(leftChild(pos));
                }
                
                else 
                { 
                    array[pos].dvalue = rightChild(pos);
                    array[rightChild(pos)].dvalue = pos;
                    swap(pos, rightChild(pos)); 
                    sinkDown(rightChild(pos));
                }
            } 
        }
    }
    
    private void floatUp(int index) 
    {
        int parent = parent(index);
        
        while (parent >= 0) 
        {
            if (this.array[index].dvalue < this.array[parent].dvalue && this.array[index].dvalue > 0) 
            {
                Vertex temp = this.array[parent];
                this.array[parent] = this.array[index];
                this.array[index] = temp;
                this.array[parent].heap_pos = index;
                this.array[index].heap_pos = parent;
            }
            else
                break;
        }
    }
    
    public void decreaseKey(int index , Vertex newVertex)
    {
        Vertex tmp = this.array[index];
        this.array[index] = newVertex;
        this.array[newVertex.heap_pos] = tmp;
        tmp.heap_pos = newVertex.heap_pos;
        newVertex.heap_pos = index;
        floatUp(index);
    }
    
    public Vertex getVertex(int heapPos)
    {
        return this.array[heapPos];
    }
    
    public Vertex extractMin() 
    { 
        if(heapSize < 1)
        {
            return null;
        }
        else
        {
            Vertex min = array[0];
            array[0] = array[heapSize--]; 
            sinkDown(0); 
            heapSize--;
            return min; 
        }
    }
}

/******************************************************************************/

class linkedList
{
    Edge head;
    int length;
    public Edge[] array;
    private int arrayCounter;
    
    public linkedList()
    {
        this.array = new Edge[10000];
    }
    
    public void insert(linkedList list, Edge edge) 
    {
        Edge newEdge = new Edge();
        edge.next = null;
        
        if (list.head == null) 
        { 
            list.head = newEdge;
            this.array[this.arrayCounter] = newEdge;
            this.arrayCounter++;
        } 
        else 
        {
            Edge last = list.head; 
            while (last.next != null) 
            { 
                last = last.next; 
            }
            last.next = newEdge;
            this.array[this.arrayCounter] = newEdge;
            this.arrayCounter++;
        }
        this.length++;
    }
}

/******************************************************************************/