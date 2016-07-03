package com.mattbarta.decision4j.trees.io;

import com.mattbarta.decisoin4j.trees.Node;
import com.mattbarta.decision4j.trees.decisiontree.DecisionNode;
import com.mattbarta.decision4j.trees.decisiontree.DecisionRule;
import com.mattbarta.decision4j.trees.decisiontree.DecisionTreeModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A backup class to save and load trees from strings in files.
 */
public class StringIO
{

    public static void saveToOutputStream(OutputStream out,
            DecisionTreeModel dtm) throws IOException
    {
        String newLine = System.getProperty("line.separator");

        try (OutputStreamWriter osw = new OutputStreamWriter(out))
        {
            Deque<Node> stack = new ArrayDeque<>();
            stack.add(dtm.getRoot());

            while (!stack.isEmpty())
            {
                Node at = stack.poll();
                List<Node> children = at.getChildren();
                if (children != null)
                {
                    stack.addAll(children);
                }
                Node parent = at.getParent();
                int parentId = 0;
                if (parent != null)
                {
                    parentId = parent.getId();
                }
                int id = at.getId();

                String string = at.toString();

                osw.write(String.valueOf(parentId));
                osw.write('\t');
                osw.write(String.valueOf(id));
                osw.write('\t');
                osw.write(string);
                osw.write(newLine);
            }
        }
    }

    public static void readInputStream(InputStream is, DecisionTreeModel dtm) throws
            IOException
    {
        Map<Integer, DecisionNode> nodes = new HashMap<>();

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
            {
                //first node should be root, which has a null parent.
                DecisionNode node = readDecisionNode(line, nodes);
                nodes.put(node.getId(), node);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        DecisionNode root = nodes.get(0);
        dtm.setRoot(root);
        dtm.setNodeCount(nodes.size());
    }

    private static DecisionNode readDecisionNode(String str,
            Map<Integer, DecisionNode> nodes)
    {
        DecisionNode node = new DecisionNode();

        Map<String, String> parts = deconstructDecisionString(str);

        int parentId = Integer.valueOf(parts.get("parentId"));
        int id = Integer.valueOf(parts.get("id"));
        String dec = parts.get("decision");

        String[] split = dec.split(";");

        String featureIndex = split[0];
        int fi = Integer.parseInt(featureIndex);
        String decStr = split[1];
        double decision = Double.parseDouble(decStr);

        String scoresStr = split[2];
        double[] scores = readScores(scoresStr);

        node.setScores(scores);
        node.setId(id);

        DecisionRule rule = new DecisionRule(fi, decision);
        node.setRule(rule);

        
        boolean knownParent = nodes.containsKey(parentId);

        if (knownParent)  // this skips root.
        {
            DecisionNode parent = nodes.getOrDefault(parentId, null);
            node.setParent(parent);
            /*
             I'm working off the assumption that the tree will always be constructed
             the same way. I always set the left child first, so the id will always
             be odd.
             */
            if ((node.getId() & 1) == 0)
            {  // even number
                parent.setRightNode(node);
            }
            else  // odd number
            {
                parent.setLeftNode(node);
            }
        }

        return node;
    }

    private static Map<String, String> deconstructDecisionString(String line)
    {

        String[] parts = line.split("\t");
        Map<String, String> results = new HashMap<>();

        results.put("parentId", parts[0]);
        results.put("id", parts[1]);
        results.put("decision", parts[2]);

        return results;
    }

    private static double[] readScores(String scores)
    {
        //take off the parens that occur due to saving with Arrays.toString()
        String[] splitScores = scores.substring(1, scores.length() - 1).split(
                ",");
        double[] results = new double[splitScores.length];

        for (int i = 0; i < results.length; i++)
        {
            results[i] = Double.parseDouble(splitScores[i]);
        }
        return results;
    }

}
