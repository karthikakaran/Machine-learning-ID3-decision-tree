import java.util.*;
import java.io.*;


class treeNode{

	treeNode left;
	treeNode right;
	double entropy;
	double infoGain;
	boolean visited;
	HashMap<Integer,String> attributes;
	HashSet<Integer> indexes;
	String targetAttribute;
	int class0Count;
	int class1Count;
	int classVal;
	String classifier;

	treeNode(treeNode node){


	this.entropy=node.entropy;
	this.infoGain=node.infoGain;
	this.visited=node.visited;
	this.attributes=new HashMap<Integer,String>(node.attributes);
	this.indexes=new HashSet<Integer>(node.indexes);
	this.targetAttribute=node.targetAttribute;
	this.class0Count=node.class0Count;
	this.class1Count=node.class1Count;
	this.classVal=node.classVal;
	this.classifier=node.classifier;

	}

	treeNode(){

		classVal=-1;
		entropy=0;
		infoGain=0;
		left=null;
		visited=false;

		right=null;
		class0Count=0;
		class1Count=0;
		classifier="";
		attributes=new HashMap<Integer,String>();
		indexes=new HashSet<Integer>();
		targetAttribute="";

	}

	void setVisited(boolean val){

		visited=val;
	}

	boolean isVisited(){

		return visited;
	}

	void setIndexes(HashSet<Integer> index){

		indexes=new HashSet<Integer>(index);
	}

	HashSet<Integer> getIndexes(){

		return indexes;
	}

	void displayIndexes(){

		Iterator itr=indexes.iterator();

		System.out.println("Index Set size="+indexes.size());

		while(itr.hasNext()){

			System.out.print((Integer)itr.next()+" ");
		}

	}

	void setAttributes(HashMap<Integer,String> attributeNames){

		//attributes=attributeNames;

		attributes.putAll(attributeNames);
	}

	HashMap<Integer,String> getAttributes(){

		return attributes;
	}

	String getAttributeValue(int key){

		return attributes.get(key);
	}

	boolean isAttributesEmpty(){

		if((attributes.size()-1)>0){
			return false;
		}
		else{

			return true;
		}
	}

	void setClassVal(int val){

		classVal=val;
	}

	int getClassVal(){

		return classVal;
	}

	String getClassifier(){

		return classifier;
	}

	String getTargetAttribute(){

		return targetAttribute;
	}


	void setClassifier(String attribute){

		classifier=attribute;
	}

	int getClassCount(int classVal){

		if(classVal==0)
			return class0Count; 
		else
			return class1Count;
		
	}

	void removeAttribute(int attr){

		attributes.remove(attr);
	}


	void displayAttributes(){

		Iterator itr=attributes.entrySet().iterator();
		System.out.println("No. of Attributes in this node:"+attributes.size());

		while(itr.hasNext()){

			Map.Entry pair=(Map.Entry)itr.next();
			System.out.println(pair.getKey()+":"+pair.getValue());
		}
	}


	void setTargetAttribute(String target){

		targetAttribute=target;
	}


	void setClassCountRoot(HashMap<Integer,HashMap<String,Integer>> records){

		class0Count=class1Count=0;

		Iterator itr=records.entrySet().iterator();

		HashMap<String,Integer> innerMap;
	
		while(itr.hasNext()){

			Map.Entry pair=(Map.Entry) itr.next();			

			innerMap=new HashMap<String,Integer>();

			int rowNo=(Integer)pair.getKey();

			innerMap=(HashMap<String,Integer>)pair.getValue();

			indexes.add(rowNo);
		
			if(innerMap.get(getTargetAttribute())==0){

				class0Count++;
			}
			else{

				class1Count++;
			}

		}

	}


	void setDataIndicesAndCounts(HashMap<Integer,HashMap<String,Integer>> records,String attr, int value){

		class0Count=class1Count=0;

		Iterator itr=getIndexes().iterator();

		HashSet<Integer> newSet=new HashSet<Integer>();

		HashMap<String,Integer> tempHash;

		while(itr.hasNext()){

			int rowNo=(Integer)itr.next();

			tempHash=new HashMap<String,Integer>(records.get(rowNo));


			if(tempHash.get(attr)==value){

				newSet.add(rowNo);

				if(tempHash.get(getTargetAttribute())==0) {

					class0Count++;
				}
				else{

					class1Count++;
				}
			
			}

		}

		indexes.clear();
		indexes=new HashSet<Integer>(newSet);
	}

	void computeEntropy(){

		int total=(class0Count+class1Count);
		
		if(total==0){
			entropy=0;
		}
		else{
			double prob0=(double)class0Count/total;
			double prob1=(double)class1Count/total;
			double product0=(prob0*(log(prob0,2))*-1);
			double product1=(prob1*(log(prob1,2))*-1);

			entropy=(double)product0+product1;
		}

	}


	static double log(double x, int base){
		if(x!=0)
    		return (Math.log(x) / Math.log(base));
    	else
    		return 0;
	}

	void computeInfoGain(){

		if(left==null && right==null){

 			infoGain=1.0;

		}	
		else{

			double entropyLeft;
			double entropyRight;

			left.computeEntropy();
			entropyLeft=left.getEntropy();

			right.computeEntropy();
			entropyRight=right.getEntropy();

			int totalLeft=left.getClassCount(0)+left.getClassCount(1);
			int totalRight=right.getClassCount(0)+right.getClassCount(1);

			computeEntropy();

			int total=class0Count+class1Count;
			double probLeft=(double)totalLeft/total;
			double probRight=(double)totalRight/total;
			double product1= (double)probLeft* entropyLeft;
			double product2= (double)probRight* entropyRight;
			infoGain=(double)entropy-(product1+product2);

		}
	}

	treeNode getLeftNode(){

		return left;
	}

	treeNode getRightNode(){

		return right;
	}

	void setLeftNode(treeNode node){

		left=node;
	}


	void setRightNode(treeNode node){

		right=node;
	}


	void setEntropy(double value){

		entropy=value;
	}


	void setInfoGain(double value){

		infoGain=value;
	}


	double getInfoGain(){

		return infoGain;
	}
	
	double getEntropy(){

		return entropy;
	}

}


/*------------------------------------------------------------------------------------------------------------------------------------*/
/*Decision Tree Class                                                                                                                 */
/*-------------------------------------------------------------------------------------------------------------------------------------*/


class decisionTree{

	treeNode root;	
	LinkedList<treeNode> nodeList;
	int size;

	decisionTree(){

		root=null;
		size=0;
	}

	decisionTree(decisionTree orig){

		//do a deep copy 
		root=new treeNode(orig.getRoot());

		root.setLeftNode(null);
		root.setRightNode(null);

		nodeList=new LinkedList<treeNode>(orig.nodeList);

		copyTreeNodes(orig.getRoot(),root);
	}

	void copyTreeNodes(treeNode origNode, treeNode currNode){

		if(origNode==null || currNode==null){

			return;
		}

		if(origNode.getLeftNode()!=null){

			treeNode newNode=new treeNode(origNode.getLeftNode());	
			currNode.setLeftNode(newNode);
		}


		if(origNode.getRightNode()!=null){

			treeNode newNode=new treeNode(origNode.getRightNode());	
			currNode.setRightNode(newNode);
		}

		copyTreeNodes(origNode.getLeftNode(),currNode.getLeftNode());

		copyTreeNodes(origNode.getRightNode(),currNode.getRightNode());
		
	}

	decisionTree(instanceDB trainingSet){
	
	nodeList=new LinkedList<treeNode>();
	root=null;
	size=0;

	//create root node
	root=new treeNode();

	//set target Attribute

	root.setAttributes(trainingSet.getAttributeNames());

	HashMap<Integer,String> tempHash=root.getAttributes();
	
	root.setTargetAttribute(tempHash.get(tempHash.size()-1));
	//set class Counts

	root.setClassCountRoot(trainingSet.getRecords());

	//calculate entropy
	root.computeEntropy();

	//check whether all positive classes (i.e. 1s)
	if(trainingSet.getSize()==root.getClassCount(1)){
		root.setClassifier("Root");
		root.setClassVal(1);
		return;
	}

	//check whether all negative classes (i.e. 1s)
	if(trainingSet.getSize()==root.getClassCount(0)){
		root.setClassifier("Root");
		root.setClassVal(0);
		return;
	}

	if(root.isAttributesEmpty()){
		
		if(root.getClassCount(0)>root.getClassCount(1)){

			root.setClassifier("Root");
			root.setClassVal(0);
		}
		else{

			root.setClassifier("Root");
			root.setClassVal(1);
		}

		return;
	}

	root.setLeftNode(null);
	root.setRightNode(null);

	buildTree(trainingSet,root);
	
	}


	void display(treeNode node,int level){


		if(node==null){
			return;
		}

		level++;

		if(node.getLeftNode()!=null){
			
			treeNode left=node.getLeftNode();

			int i=0;

			while(i<level){

				System.out.print("| ");
				i++;
			}

			if(left.getClassVal()==-1){
				
				System.out.println(node.getClassifier()+" = 0 :");
			}
			else{

				System.out.println(node.getClassifier()+" = 0 : "+left.getClassVal());
			}

				display(left,level);
		}


		if(node.getRightNode()!=null){
			
			treeNode right=node.getRightNode();

			int i=0;

			while(i<level){

				System.out.print("| ");
				i++;
			}

			if(right.getClassVal()==-1){
				
				System.out.println(node.getClassifier()+" = 1 :");
			}
			else{

				System.out.println(node.getClassifier()+" = 1 : "+right.getClassVal());
			}
		

			display(right,level);

		}

	}

	void buildTree(instanceDB trainingSet, treeNode node){
	
		if(node==null){

			return;
		}

		if(node.getClassCount(0)==(node.getClassCount(0)+node.getClassCount(1))){

				node.setClassVal(0);
				return;
		}

		if(node.getClassCount(1)==(node.getClassCount(0)+node.getClassCount(1))){

			node.setClassVal(1);
			return;
		}

		//stop building when there are no more attributes to Classify

		if(node.isAttributesEmpty()){
			
			if(node.getClassCount(0)>node.getClassCount(1)){

				node.setClassVal(0);

			}
			else{

				node.setClassVal(1);	
			}
			
			return;
		}
		

		int attrVal=findBestAttribute(trainingSet,node);

		//set the best attribute as the classfier attribute
		node.setClassifier(node.getAttributeValue(attrVal));

		node.computeEntropy();

		HashMap<Integer,String> tempHashNode=new HashMap<Integer,String>();

		tempHashNode.putAll(node.getAttributes());

		//create left node
		treeNode left=new treeNode();

		left.setAttributes(tempHashNode);
	
		left.setTargetAttribute(node.getTargetAttribute());

		left.setIndexes(node.getIndexes());

		left.setDataIndicesAndCounts(trainingSet.getRecords(), left.getAttributeValue(attrVal), 0);

		left.computeEntropy();

		left.removeAttribute(attrVal);

		//create right node
		treeNode right=new treeNode();

		right.setAttributes(tempHashNode);

		right.setTargetAttribute(node.getTargetAttribute());

		right.setIndexes(node.getIndexes());

		right.setDataIndicesAndCounts(trainingSet.getRecords(), right.getAttributeValue(attrVal), 1);

		right.computeEntropy();

		right.removeAttribute(attrVal);
		
		//setup subtree for left node

		if(left.getIndexes().size()>0){
	
			node.setLeftNode(left);
			buildTree(trainingSet,left);
	
		}
		
		//setup subtree for right node

		if(right.getIndexes().size()>0){
	
			node.setRightNode(right);
			buildTree(trainingSet,right);
	
		}
	}

	int findBestAttribute(instanceDB instance, treeNode node){


	//calculate Information gain for all attributes
	//select attribute having maximum Information Gain
	//return attribute

		double max=Double.NEGATIVE_INFINITY;
		int maxIndex=-1, key=-1;

		String temp="";

		Iterator itr=sortByValues(node.getAttributes()).entrySet().iterator();

		while(itr.hasNext()){

			Map.Entry pair=(Map.Entry)itr.next();

			key=(Integer)pair.getKey();
			String value=(String)pair.getValue();

			if(node.getTargetAttribute()!=value){
				double k=buildAndCalculateIG(instance,value,node);

				if(k>max){
					max=k;
					maxIndex=key;
					temp=value;
				}
			}
		}

	node.setLeftNode(null);
	node.setRightNode(null);
	node.setInfoGain(max);

	return maxIndex;
} 

	double buildAndCalculateIG(instanceDB instance, String attrVal,treeNode node){


		treeNode left=new treeNode();		

		left.setTargetAttribute(node.getTargetAttribute());

		left.setIndexes(node.getIndexes());

		left.setDataIndicesAndCounts(instance.getRecords(), attrVal,0);

		left.computeEntropy();

		treeNode right=new treeNode();		

		right.setTargetAttribute(node.getTargetAttribute());

		right.setIndexes(node.getIndexes());

		right.setDataIndicesAndCounts(instance.getRecords(), attrVal,1);

		right.computeEntropy();

		node.setLeftNode(left);
		node.setRightNode(right);

		node.computeInfoGain();

		return node.getInfoGain();
	}


	int getSize(){

		return size;
	}


	treeNode getRoot(){

		return root;
	}


public static void main(String[] args) throws IOException{

	int L=0, K=0;
	String print="";
	String trainSet="";
	String validSet="";
	String testSet="";

	if(args.length>=6){
	
	 try{
			if(args[0].length()>0){

				L=Integer.parseInt(args[0]);
			}
			else{

				System.out.println("Please provide input:<L>");
				return;
			}

			if(args[1].length()>0){

				K=Integer.parseInt(args[1]);
			}
			else{

				System.out.println("Please provide input:<K>");
				return;
			}

		}
		catch(NumberFormatException e){

			System.out.println("Please provide valid input:<L> and <K>");
			return;
		}		

		if(args[2].length()>0){

			trainSet=args[2];
		}
		else{

				System.out.println("Please provide input:<training-set>");
				return;
		}


		if(args[3].length()>0){

			validSet=args[3];
		}
		else{

			System.out.println("Please provide input:<validation-set>");
			return;
		}


		if(args[4].length()>0){

			testSet=args[4];
		}
		else{

			System.out.println("Please provide input:<test-set>");
			return;
		}


		if(args[5].length()>0){

			print=args[5];
		}
		else{

			System.out.println("Please provide input:<to-print>");
			return;
		}


	}
	else{
		System.out.println("Please input the parameters in order below:");
		System.out.println("<L> <K> <training-set> <validation-set> <test-set> <to-print>");
		System.out.println("L: integer (used in the post-pruning algorithm)");
		System.out.println("K: integer (used in the post-pruning algorithm)");
		System.out.println("to-print:{yes,no}");

		return;
	}

	instanceDB trainingSet=new instanceDB(trainSet);

	decisionTree tree=new decisionTree(trainingSet);

	instanceDB validationSet=new instanceDB(validSet);

	instanceDB testingSet=new instanceDB(testSet);

	//System.out.println("Accuracy of training Set="+(tree.accuracy(trainingSet)*100)+"%");
	
	System.out.println("Decision Tree constructed successfully using Training set<"+trainSet+">");	

	//double accuracy=(double) Math.round(tree.accuracy(validationSet)*10000)/100;

	double accuracy=(double) Math.round(tree.accuracy(testingSet)*10000)/100;

	System.out.println("Accuracy of decision tree on Test Set<"+testSet+"> before pruning="+accuracy+"%");

	//System.out.println("Accuracy of decision tree on Validation Set<"+validSet+">"+" before pruning="+accuracy+"%");

	decisionTree copyTree=new decisionTree(tree);

	decisionTree prunedTree=tree.prune(L,K,validationSet);

	//accuracy=(double) Math.round(prunedTree.accuracy(validationSet)*10000)/100;

	//System.out.println("Accuracy of pruned decision tree on Validation Set<"+validSet+"> ="+accuracy+"%");

	accuracy=(double) Math.round(prunedTree.accuracy(testingSet)*10000)/100;

	System.out.println("Accuracy of pruned decision tree on Test Set<"+testSet+"> ="+accuracy+"%");

	if(print.equals("yes")){

		prunedTree.display(prunedTree.getRoot(),-1);
	}

	}


	double accuracy(instanceDB instances){

		Iterator itr=instances.getRecords().entrySet().iterator();

		HashMap<String,Integer> tempHash;

		int correctCounter=0, total=0;

		while(itr.hasNext()){

			Map.Entry entry=(Map.Entry) itr.next();

			tempHash=new HashMap<String,Integer>((HashMap<String,Integer>)entry.getValue());

			int predictedVal=getPredictedValue(root,tempHash);
			int actualVal=tempHash.get(root.getTargetAttribute());

			if(predictedVal==actualVal){

				correctCounter++;
			}

			total++;
		}

		return (double)correctCounter/total;
	}


	int getPredictedValue(treeNode node, HashMap<String,Integer> tempHash){

			if(node==null){

				return -1;
			}


			if(node.getLeftNode()==null && node.getRightNode()==null){

				return node.getClassVal();
			}


			if(tempHash.get(node.getClassifier())==0){

				if(node.getLeftNode()!=null){

					return getPredictedValue(node.getLeftNode(), tempHash);
				}
				
			}

			if(tempHash.get(node.getClassifier())==1){

				if(node.getRightNode()!=null){

					return getPredictedValue(node.getRightNode(), tempHash);
				}
	
			}

			return -1;
	}

void initList(){

	nodeList=new LinkedList<treeNode>();

}


decisionTree prune(int L, int K, instanceDB instances){

	decisionTree bestTree=new decisionTree(this);

	double bestAccuracy=bestTree.accuracy(instances);

	double accuracy=0;

	for(int i=1;i<=L;i++){

		decisionTree currentTree=new decisionTree(this);

		Random rand=new Random();
		int M=rand.nextInt(K)+1;

		for(int j=1;j<=M;j++){

			LinkedList<treeNode> list=new LinkedList<treeNode>();
			
			list.add(currentTree.getRoot());

			currentTree.initList();

			currentTree.levelOrder(list);

			int N=currentTree.nodeList.size();

			Random rand1=new Random();
			int P=rand1.nextInt(N);

			//if root node, skip.
			if(P==0){

				continue;
			}

			if(!currentTree.nodeList.get(P).visited){

				treeNode node=currentTree.nodeList.get(P);

				treeNode newNode=new treeNode();

				node.setVisited(true);

				node.setLeftNode(null);

				node.setRightNode(null);
				
				node.setLeftNode(newNode);

				if(node.getClassCount(0)>node.getClassCount(1)){

					newNode.setClassVal(0);

				}
				else{

					newNode.setClassVal(1);
				}

			}
			else{

				continue;
			}

		}

		//calculate accuracy of currentTree

		accuracy=currentTree.accuracy(instances);


		if(accuracy>bestAccuracy){

			bestAccuracy=accuracy;

			bestTree=new decisionTree(currentTree);

		}

	}

return bestTree;

}




void displayList(){

	for(int i=0;i<nodeList.size();i++){

		if(i==nodeList.size()-1)
			System.out.print(nodeList.get(i).getClassCount(0)+":"+nodeList.get(i).getClassCount(1));
		else	
			System.out.print(nodeList.get(i).getClassCount(0)+":"+nodeList.get(i).getClassCount(1)+"->");
	}
	System.out.println(" ");
}


private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
            }
       });

       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }


	void levelOrder(LinkedList<treeNode> list){

		if(list.size()==0){

			return;
		}

		LinkedList<treeNode> newList=new LinkedList<treeNode>();

		for(int i=0;i<list.size();i++){

			if(list.get(i).getLeftNode()==null && list.get(i).getRightNode()==null){

					continue;
			}
			else{

				if(list.get(i).getLeftNode()!=null){

					newList.addLast(list.get(i).getLeftNode());

				}
				
				if(list.get(i).getRightNode()!=null){

					newList.addLast(list.get(i).getRightNode());
				}

				nodeList.addLast(list.get(i));
			}

		}

		levelOrder(newList);

	}

}

class instanceDB{

	HashMap<Integer,String> attributeNames;
	int size;
	HashMap<Integer,HashMap<String,Integer>> records;

	instanceDB(){
		attributeNames=new HashMap<Integer,String>();
		records=new HashMap<Integer,HashMap<String,Integer>>();		
		size=0;
	}

	HashMap<Integer,HashMap<String,Integer>> getRecords(){

		return records;
	}

	HashMap<Integer,String> getAttributeNames(){

		return attributeNames;
	}

	int getSize(){

		return size;
	}

	instanceDB(String fileLocation) throws IOException{

		BufferedReader reader=new BufferedReader(new FileReader(fileLocation));

		size=0;
		String line="";
		int i=0;
		attributeNames=new HashMap<Integer,String>();
		records=new HashMap<Integer,HashMap<String,Integer>>();
		boolean first=true;

		while((line=reader.readLine())!=null && line.length()!=0){

			if(first){
				addAttributeNames(line);
				first=false;
			}
			else{
				addRecordsToHashMap(line,i);
				size++;
				i++;
			}

		}

		reader.close();

	}

	void addAttributeNames(String line){

		String[] array=splitCSVArray(line);

		for(int i=0;i<array.length;i++){

			attributeNames.put(i,array[i]);
		}

	}

	void addRecordsToHashMap(String line,int row){

		HashMap<String,Integer> attribute;
		attribute=new HashMap<String,Integer>();

		for(int j=0,i=0;i<attributeNames.size();j=j+2,i++){

			attribute.put(attributeNames.get(i),Character.getNumericValue(line.charAt(j)));
			//System.out.println(attributeNames[i]+" "+attribute.get(attributeNames[i]));

		}

		records.put(row,attribute);

	}

	void displayInstances(){

		
		System.out.println("Total No. of records="+records.size());
		Iterator itr=records.entrySet().iterator();

		while(itr.hasNext()){
			Map.Entry pair=(Map.Entry) itr.next();
			
			System.out.println(pair.getKey()+":");

			HashMap<String,Integer> innerMap;

			innerMap=(HashMap<String,Integer>)pair.getValue();

			Iterator innerItr=innerMap.entrySet().iterator();
			
			while(innerItr.hasNext()){

				Map.Entry innerPair=(Map.Entry) innerItr.next();
				System.out.print(innerPair.getKey()+":"+innerPair.getValue()+",");				

			}
			System.out.println(" ");				
		}
	}

	String[] splitCSVArray(String line){

		return line.split(",");

	}
}
