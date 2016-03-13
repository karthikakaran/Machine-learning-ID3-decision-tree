import java.util.*;
import java.io.*;

class Tree{
	Tree left;
	Tree right;
	double entropy;
	double infoGain;
	boolean checked;
	double varianceImp;
	double infoGainVarImp;
	
	HashMap<Integer,String> allAttribs;
	HashSet<Integer> indexes;
	String targetAttrib;
	int class0Count;
	int class1Count;
	int valuesClass;
	String identity;
	
	Tree(Tree node){
		this.entropy=node.entropy;
		this.infoGain=node.infoGain;
		this.varianceImp=node.varianceImp;
		this.infoGainVarImp=node.infoGainVarImp;
		this.checked=node.checked;
		this.allAttribs=new HashMap<Integer,String>(node.allAttribs);
		this.indexes=new HashSet<Integer>(node.indexes);
		this.targetAttrib=node.targetAttrib;
		this.class0Count=node.class0Count;
		this.class1Count=node.class1Count;
		this.valuesClass=node.valuesClass;
		this.identity=node.identity;
	}
	Tree(){
		valuesClass=-1;
		entropy=0;
		infoGain=0;
		varianceImp = 0;
		infoGainVarImp = 0;
		left=null;
		checked=false;
		right=null;
		class0Count=0;
		class1Count=0;
		identity="";
		allAttribs=new HashMap<Integer,String>();
		indexes=new HashSet<Integer>();
		targetAttrib="";
	}
	void setChecked(boolean val){
		checked=val;
	}
	boolean isChecked(){
		return checked;
	}
	void setIndexes(HashSet<Integer> index){
		indexes=new HashSet<Integer>(index);
	}
	HashSet<Integer> getIndexes(){
		return indexes;
	}
	void setAttributes(HashMap<Integer,String> attributeNames){
		allAttribs.putAll(attributeNames);
	}
	HashMap<Integer,String> getAttributes(){
		return allAttribs;
	}
	String getAllAttribs(int key){
		return allAttribs.get(key);
	}
	boolean isAttribEmpty(){
		if((allAttribs.size()-1)>0){
			return false;
		}
		else{
			return true;
		}
	}
	void setValuesClass(int val){
		valuesClass=val;
	}
	int getValuesClass(){
		return valuesClass;
	}
	String getIdentity(){
		return identity;
	}
	String getTargetAttribute(){
		return targetAttrib;
	}
	void setIdentify(String attribute){
		identity=attribute;
	}
	int getClassCount(int classVal){
		if(classVal==0)
			return class0Count; 
		else
			return class1Count;
	}
	void removeAttribute(int attr){
		allAttribs.remove(attr);
	}
	void setTargetAttrib(String target){
		targetAttrib=target;
	}
	void rootClassCount(HashMap<Integer,HashMap<String,Integer>> records){
		class0Count=0;
		class1Count=0;
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
	void IndicesAndCounts(HashMap<Integer,HashMap<String,Integer>> records,String attr, int value){
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
	void currentEntropy(){
		int total=(class0Count+class1Count);
		if(total==0){
			entropy=0;
		}
		else{
			double prob0=(double)class0Count/total;
			double prob1=(double)class1Count/total;
			double logBase1 = 0;
			if(prob0 != 0)
				logBase1 = Math.log(prob0) / Math.log(2);
			double product0=(prob0*logBase1*-1);
			
			double logBase2 = 0;
			if(prob1 != 0)
				logBase2 = Math.log(prob1) / Math.log(2);
			double product1=(prob1*logBase2*-1);
			
			entropy=(double)product0+product1;
		}
	}
	void varImpurity(){
		int total=(class0Count+class1Count);
		if(total==0){
			varianceImp=0;
		}
		else{
			double prob0=(double)class0Count/total;
			double prob1=(double)class1Count/total;
			varianceImp=(double)prob0*prob1;
		}
	}
	void informationGain(){
		if(left==null && right==null){
 			infoGain=1.0;
		}	
		else{
			double entropyLeft;
			double entropyRight;
			left.currentEntropy();
			entropyLeft=left.getEntropy();
			right.currentEntropy();
			entropyRight=right.getEntropy();
			int totalLeft=left.getClassCount(0)+left.getClassCount(1);
			int totalRight=right.getClassCount(0)+right.getClassCount(1);
			currentEntropy();
			int total=class0Count+class1Count;
			double probLeft=(double)totalLeft/total;
			double probRight=(double)totalRight/total;
			double product1= (double)probLeft* entropyLeft;
			double product2= (double)probRight* entropyRight;
			infoGain=(double)entropy-(product1+product2);
		}
	}
	void varianceImpurityInfoGain(){
		if(left==null && right==null){
 			varianceImp=1.0;
		}	
		else{
			double varImpLeft;
			double varImpRight;
			left.varImpurity();
			varImpLeft=left.getVarianceImp();
			right.varImpurity();
			varImpRight=right.getVarianceImp();
			int totalLeft=left.getClassCount(0)+left.getClassCount(1);
			int totalRight=right.getClassCount(0)+right.getClassCount(1);
			varImpurity();
			int total=class0Count+class1Count;
			double probLeft=(double)totalLeft/total;
			double probRight=(double)totalRight/total;
			double product1= (double)probLeft* varImpLeft;
			double product2= (double)probRight* varImpRight;
			infoGainVarImp=(double)varianceImp-(product1+product2);
		}
	}
	Tree getLeftNode(){
		return left;
	}
	Tree getRightNode(){
		return right;
	}
	void setLeftNode(Tree node){
		left=node;
	}
	void setRightNode(Tree node){
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
	double getVarianceImp() {
		return varianceImp;
	}
	void setVarianceImp(double varianceImp) {
		this.varianceImp = varianceImp;
	}
	double getInfoGainVarImp() {
		return infoGainVarImp;
	}
	void setInfoGainVarImp(double infoGainVarImp) {
		this.infoGainVarImp = infoGainVarImp;
	}
}
class recordsFetch{
	HashMap<Integer,String> attributeNames;
	int size;
	HashMap<Integer,HashMap<String,Integer>> rowRecords;
	recordsFetch(){
		attributeNames=new HashMap<Integer,String>();
		rowRecords=new HashMap<Integer,HashMap<String,Integer>>();		
		size=0;
	}
	HashMap<Integer,HashMap<String,Integer>> getRecords(){
		return rowRecords;
	}
	HashMap<Integer,String> getAttributeNames(){
		return attributeNames;
	}
	int getSize(){
		return size;
	}
	void loadRowsHashMap(String line,int row){
		HashMap<String,Integer> attribute;
		attribute=new HashMap<String,Integer>();
		for(int j=0,i=0;i<attributeNames.size();j=j+2,i++){
			attribute.put(attributeNames.get(i),Character.getNumericValue(line.charAt(j)));
		}
		rowRecords.put(row,attribute);
	}
	recordsFetch(String fileLocation) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(fileLocation));
		size=0;
		String line="";
		int i=0;
		attributeNames=new HashMap<Integer,String>();
		rowRecords=new HashMap<Integer,HashMap<String,Integer>>();
		boolean first=true;
		while((line=reader.readLine())!=null && line.length()!=0){
			if(first){
				String[] array= line.split(",");
				for(int j=0;j < array.length; j++){
					attributeNames.put(j,array[j]);
				}
				first=false;
			}
			else{
				loadRowsHashMap(line,i);
				size++;
				i++;
			}
		}
		reader.close();
	}
}
class Id3decision{
	Tree root;	
	LinkedList<Tree> listOfNodes;
	int size;
	Id3decision(){
		root=null;
		size=0;
	}
	Id3decision(Id3decision mainRoot){
		root=new Tree(mainRoot.getRoot());
		root.setLeftNode(null);
		root.setRightNode(null);
		listOfNodes=new LinkedList<Tree>(mainRoot.listOfNodes);
		loadNodes(mainRoot.getRoot(),root);
	}
	void loadNodes(Tree mainNode, Tree currNode){
		if(mainNode==null || currNode==null){
			return;
		}
		if(mainNode.getLeftNode()!=null){
			Tree newNode=new Tree(mainNode.getLeftNode());	
			currNode.setLeftNode(newNode);
		}
		if(mainNode.getRightNode()!=null){
			Tree newNode=new Tree(mainNode.getRightNode());	
			currNode.setRightNode(newNode);
		}
		loadNodes(mainNode.getLeftNode(),currNode.getLeftNode());
		loadNodes(mainNode.getRightNode(),currNode.getRightNode());
	}
	Id3decision(recordsFetch trainingSet, String heurisOpt){
		listOfNodes=new LinkedList<Tree>();
		root=null;
		size=0;
		root=new Tree();
		root.setAttributes(trainingSet.getAttributeNames());
		HashMap<Integer,String> tempHash=root.getAttributes();
		root.setTargetAttrib(tempHash.get(tempHash.size()-1));
	
		root.rootClassCount(trainingSet.getRecords());
		if(heurisOpt.equals("1"))
			root.currentEntropy();
		else if(heurisOpt.equals("2"))
			root.varImpurity();
		if(trainingSet.getSize()==root.getClassCount(1)){
			root.setIdentify("Root");
			root.setValuesClass(1);
			return;
		}
		if(trainingSet.getSize()==root.getClassCount(0)){
			root.setIdentify("Root");
			root.setValuesClass(0);
			return;
		}
		if(root.isAttribEmpty()){
			if(root.getClassCount(0)>root.getClassCount(1)){
				root.setIdentify("Root");
				root.setValuesClass(0);
			}
			else{
				root.setIdentify("Root");
				root.setValuesClass(1);
			}
			return;
		}
		root.setLeftNode(null);
		root.setRightNode(null);
		constructTree(trainingSet,root, heurisOpt);
    }
	
	void getIndentForLevel(int level) {
		String indent = "";
		for (int i = 1; i <= level; i++) {
			System.out.print("| ");
		}
	}
	void display(Tree node,int level){
		if(node==null){
			return;
		}
		level = level + 1;
		if(node.getLeftNode()!=null){
			Tree left=node.getLeftNode();
			getIndentForLevel(level);
			if(left.getValuesClass()==-1){
				System.out.println(node.getIdentity()+" = 0 :");
			}
			else{
				System.out.println(node.getIdentity()+" = 0 : "+left.getValuesClass());
			}
				display(left,level);
		}
		if(node.getRightNode()!=null){
			Tree right=node.getRightNode();
			getIndentForLevel(level);
			if(right.getValuesClass()==-1){
				System.out.println(node.getIdentity()+" = 1 :");
			}
			else{
				System.out.println(node.getIdentity()+" = 1 : "+right.getValuesClass());
			}
			display(right,level);
		}
	}
	void constructTree(recordsFetch trainingSet, Tree node, String heurisOpt){
		if(node==null){
			return;
		}
		if(node.getClassCount(0)==(node.getClassCount(0)+node.getClassCount(1))){
				node.setValuesClass(0);
				return;
		}
		if(node.getClassCount(1)==(node.getClassCount(0)+node.getClassCount(1))){
			node.setValuesClass(1);
			return;
		}
		if(node.isAttribEmpty()){
			if(node.getClassCount(0)>node.getClassCount(1)){
				node.setValuesClass(0);
			}
			else{
				node.setValuesClass(1);	
			}
			return;
		}
		int attrVal=chooseBestInfoGainAttrib(trainingSet,node, heurisOpt);
		node.setIdentify(node.getAllAttribs(attrVal));
		if(heurisOpt.equals("1"))
			node.currentEntropy();
		else if(heurisOpt.equals("2"))
			node.varImpurity();
		HashMap<Integer,String> tempHashNode=new HashMap<Integer,String>();
		tempHashNode.putAll(node.getAttributes());
		Tree left=new Tree();
		left.setAttributes(tempHashNode);
		left.setTargetAttrib(node.getTargetAttribute());
		left.setIndexes(node.getIndexes());
		left.IndicesAndCounts(trainingSet.getRecords(), left.getAllAttribs(attrVal), 0);
		if(heurisOpt.equals("1"))
			left.currentEntropy();
		else if(heurisOpt.equals("2"))
			left.varImpurity();
		left.removeAttribute(attrVal);
		
		Tree right=new Tree();
		right.setAttributes(tempHashNode);
		right.setTargetAttrib(node.getTargetAttribute());
		right.setIndexes(node.getIndexes());
		right.IndicesAndCounts(trainingSet.getRecords(), right.getAllAttribs(attrVal), 1);
		if(heurisOpt.equals("1"))
			right.currentEntropy();
		else if(heurisOpt.equals("2"))
			right.varImpurity();
		right.removeAttribute(attrVal);
	
		if(left.getIndexes().size()>0){
			node.setLeftNode(left);
			constructTree(trainingSet,left, heurisOpt);
		}
		
		if(right.getIndexes().size()>0){
			node.setRightNode(right);
			constructTree(trainingSet,right,heurisOpt);
		}
	}
	int chooseBestInfoGainAttrib(recordsFetch instance, Tree node, String heurisOpt){

		double max=Double.NEGATIVE_INFINITY;
		int maxIndex=-1, key=-1;
		String temp="";
		Iterator itr=valueBasedSorting(node.getAttributes()).entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry pair=(Map.Entry)itr.next();
			key=(Integer)pair.getKey();
			String value=(String)pair.getValue();
			if(node.getTargetAttribute()!=value){
				double k = 0;
				if(heurisOpt.equals("1"))
					k=buildInfoGain(instance,value,node);
				else if(heurisOpt.equals("2"))
					k=buildInfoGainImpVar(instance,value,node);
				if(k>max){
					max=k;
					maxIndex=key;
					temp=value;
				}
			}
		}
	node.setLeftNode(null);
	node.setRightNode(null);
	if(heurisOpt.equals("1"))
		node.setInfoGain(max);
	else if(heurisOpt.equals("2"))
		node.setInfoGainVarImp(max);
	return maxIndex;
} 
	double buildInfoGain(recordsFetch instance, String attrVal,Tree node){
		Tree left=new Tree();		
		left.setTargetAttrib(node.getTargetAttribute());
		left.setIndexes(node.getIndexes());
		left.IndicesAndCounts(instance.getRecords(), attrVal,0);
		left.currentEntropy();
		Tree right=new Tree();		
		right.setTargetAttrib(node.getTargetAttribute());
		right.setIndexes(node.getIndexes());
		right.IndicesAndCounts(instance.getRecords(), attrVal,1);
		right.currentEntropy();
		node.setLeftNode(left);
		node.setRightNode(right);
		node.informationGain();
		return node.getInfoGain();
	}
	double buildInfoGainImpVar(recordsFetch instance, String attrVal,Tree node){
		Tree left=new Tree();		
		left.setTargetAttrib(node.getTargetAttribute());
		left.setIndexes(node.getIndexes());
		left.IndicesAndCounts(instance.getRecords(), attrVal,0);
		left.varImpurity();
		Tree right=new Tree();		
		right.setTargetAttrib(node.getTargetAttribute());
		right.setIndexes(node.getIndexes());
		right.IndicesAndCounts(instance.getRecords(), attrVal,1);
		right.varImpurity();
		node.setLeftNode(left);
		node.setRightNode(right);
		node.varianceImpurityInfoGain();
		return node.getInfoGainVarImp();
	}
	int getSize(){
		return size;
	}
	Tree getRoot(){
		return root;
	}
public static void main(String[] args) throws IOException{
	int L=0, K=0;
	int lval=0, kval=0;
	String printOpt="";
	String train_Set="";
	String valid_Set="";
	String test_Set="";
	String heuristicOpt[] = {"1","2"};
	if(args.length>=6){
		lval=Integer.parseInt(args[0]);
		kval=Integer.parseInt(args[1]);
		train_Set=args[2];
		valid_Set=args[3];
		test_Set=args[4];
		printOpt=args[5];
	} else{
		System.out.println("Please input the parameters in order below:");
		System.out.println("<L> <K> <training-set> <validation-set> <test-set> <to-print>");
		System.out.println("L: integer (used in the post-pruning algorithm)");
		System.out.println("K: integer (used in the post-pruning algorithm)");
		System.out.println("to-print:{yes,no}");
		return;
	}
	for(int i=0; i<2;i++){
	  if(i==1)
		System.out.println("L="+L+" "+"K="+K);
	  for(String ind : heuristicOpt){
		if(ind.equals("1")) {
			System.out.println("Information Gain:");
			System.out.println("===================");
		}
		else{
			System.out.println("Impurity Variance:");
			System.out.println("===================");
		}
		
		recordsFetch trainingSet=new recordsFetch(train_Set);
		Id3decision tree=new Id3decision(trainingSet, ind);
		recordsFetch validationSet=new recordsFetch(valid_Set);
		recordsFetch testingSet=new recordsFetch(test_Set);
		System.out.println("ID3 decision tree with training set");	
		double accuracy=(double) Math.round(tree.computeAccuracy(testingSet)*10000)/100;
		if(i==0){
			System.out.println("Accuracy of ID3 decision tree using test set before pruning="+accuracy+"%");
		}
		Id3decision copyTree=new Id3decision(tree);
		Id3decision prunedTree=tree.postPrune(L,K,validationSet);
		
		accuracy=(double) Math.round(prunedTree.computeAccuracy(testingSet)*10000)/100;
		if(i == 1){
			System.out.println("Accuracy of decision tree on validation set after pruning ="+accuracy+"%");
		}
		if(printOpt.equals("yes")){
			prunedTree.display(prunedTree.getRoot(),-1);
		}
	   }
       L = lval; K = kval;
	 }
   }
   double computeAccuracy(recordsFetch instances){
		Iterator itr=instances.getRecords().entrySet().iterator();
		HashMap<String,Integer> tempHash;
		int correctCounter=0, total=0;
		while(itr.hasNext()){
			Map.Entry entry=(Map.Entry) itr.next();
			tempHash=new HashMap<String,Integer>((HashMap<String,Integer>)entry.getValue());
			int predictedVal=predict(root,tempHash);
			int actualVal=tempHash.get(root.getTargetAttribute());
			if(predictedVal==actualVal){
				correctCounter++;
			}
			total++;
		}
		return (double)correctCounter/total;
	}
	int predict(Tree node, HashMap<String,Integer> tempHash){
			if(node==null){
				return -1;
			}
			if(node.getLeftNode()==null && node.getRightNode()==null){
				return node.getValuesClass();
			}
			if(tempHash.get(node.getIdentity())==0){
				if(node.getLeftNode()!=null){
					return predict(node.getLeftNode(), tempHash);
				}
			}
			if(tempHash.get(node.getIdentity())==1){
				if(node.getRightNode()!=null){
					return predict(node.getRightNode(), tempHash);
				}
			}
			return -1;
	}

Id3decision postPrune(int L, int K, recordsFetch instances){
	Id3decision bestTree=new Id3decision(this);
	double bestAccuracy=bestTree.computeAccuracy(instances);
	double accuracy=0;
	for(int i=1;i<=L;i++){
		Id3decision currentTree=new Id3decision(this);
		Random rand=new Random();
		int M=rand.nextInt(K)+1;
		for(int j=1;j<=M;j++){
			LinkedList<Tree> list=new LinkedList<Tree>();
			list.add(currentTree.getRoot());
			listOfNodes=new LinkedList<Tree>();
			currentTree.orderTheLevels(list);
			int N=currentTree.listOfNodes.size();
			Random rand1=new Random();
			int P=rand1.nextInt(N);
			
			if(P==0){
				continue;
			}
			if(!currentTree.listOfNodes.get(P).checked){
				Tree node=currentTree.listOfNodes.get(P);
				Tree newNode=new Tree();
				node.setChecked(true);
				node.setLeftNode(null);
				node.setRightNode(null);
				node.setLeftNode(newNode);
				if(node.getClassCount(0)>node.getClassCount(1)){
					newNode.setValuesClass(0);
				}
				else{
					newNode.setValuesClass(1);
				}
			}
			else{
				continue;
			}
		}
		accuracy=currentTree.computeAccuracy(instances);
		if(accuracy>bestAccuracy){
			bestAccuracy=accuracy;
			bestTree=new Id3decision(currentTree);
		}
	}
return bestTree;
}
private static HashMap valueBasedSorting(HashMap map) { 
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
	void orderTheLevels(LinkedList<Tree> list){
		if(list.size()==0){
			return;
		}
		LinkedList<Tree> newList=new LinkedList<Tree>();
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
				listOfNodes.addLast(list.get(i));
			}
		}
		orderTheLevels(newList);
	}
}
