# Decision4j
Decision Trees in Java

***

decision4j is a decision tree library written in Java.  

It's currently at version 0.1, supporting only decision trees for classification. I would like
to move this project towards common ensembling methods -- boosting and forests.

***

### Future Enhancements  

* Switch to trove structures
* Add Jackson support for IO.
* Support weights on data points better
* Refactor interfaces

***

### Usage  

DecisionTreeModelTest.java shows how to setup and run a decision tree against the iris dataset.  

```Java
CriterionFactory cf = new CriterionFactory(new CrossEntropy());
ISplitter bs = new BestSplitter(1, cf); // minSamplesInSplit
DecisionTreeFactory dtf = new DecisionTreeFactory();
DecisionTreeLearner dtl = new DecisionTreeLearner(dtf,bs, 4, 100, 100, true); // maxFeatures, maxDepth, maxLeaves, reproposeSplits

//create a new tree model
Model dtm = dtl.create(null);

//train the model        
dtl.trainEpoch(dtm, fvs);
```

I support common tree building parameters as well as whether to repropose splits for every node.