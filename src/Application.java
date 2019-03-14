import domain.DfaNode;
import domain.Graph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;
import service.RegexTreeMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Application {

    public static final String INPUT_TXT = "input.txt";

    public static void main(String[] args) {

        File file = new File(INPUT_TXT);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            String regex = sc.next();

            RegexTreeMapper regexTreeMapper = new RegexTreeMapper();
            Graph graph = regexTreeMapper.regexToTree(regex);

            int edgeCount = 0;

            DirectedSparseMultigraph<DfaNode, String> directedSparseMultigraph = new DirectedSparseMultigraph<>();
            for (DfaNode dfaNode:graph.getDfaNodeList()) {
                if(dfaNode.getData().isEmpty()) {
                    continue;
                }
                directedSparseMultigraph.addVertex(dfaNode);
                for (String neighbour : graph.getAdiacency().get(dfaNode.getData())) {
                    if(neighbour.isEmpty()) {
                        continue;
                    }
                    DfaNode neighbourNode = null;
                    for(int i = 0; i < graph.getDfaNodeList().size(); i++) {
                        if(graph.getDfaNodeList().get(i).getData().equals(neighbour)) {
                            neighbourNode = graph.getDfaNodeList().get(i);
                        }
                    }
                    Character edgeChar = (regexTreeMapper.getEdgeSymbol())[dfaNode.getId()][neighbourNode.getId()];
                    directedSparseMultigraph.addEdge(++edgeCount + "_" + edgeChar,dfaNode, neighbourNode);
                }
            }


            // Layout<V, E>, BasicVisualizationServer<V,E>
            Layout<DfaNode, String> layout = new CircleLayout(directedSparseMultigraph);
            layout.setSize(new Dimension(1000,1000));
            BasicVisualizationServer<DfaNode,String> vv =
                    new BasicVisualizationServer<DfaNode,String>(layout);
            vv.setPreferredSize(new Dimension(1050,1050));
            // Setup up a new vertex to paint transformer...
            Transformer<DfaNode,Paint> vertexPaint = new Transformer<DfaNode,Paint>() {
                public Paint transform(DfaNode i) {
                    if(i.isStart()) {
                        return Color.YELLOW;
                    }
                    if(i.isFinal()) {
                        return Color.GREEN;
                    }
                    return Color.RED;
                }
            };

            Transformer<DfaNode,Shape> vertexSize = new Transformer<DfaNode,Shape>(){
                public Shape transform(DfaNode i){
                    Ellipse2D circle = new Ellipse2D.Double(-15, -15, 50, 50);
                    // in this case, the vertex is twice as large
                    return circle;
                }
            };

            // Set up a new stroke Transformer for the edges
            float dash[] = {2.0f};
            final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 20.0f, dash, 0.0f);
            Transformer<String, Stroke> edgeStrokeTransformer =
                    new Transformer<String, Stroke>() {
                        public Stroke transform(String s) {
                            return edgeStroke;
                        }
                    };


            vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
            vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
            vv.getRenderContext().setVertexShapeTransformer(vertexSize);
            vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

            JFrame frame = new JFrame("Simple Graph View 2");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(vv);
            frame.pack();
            frame.setVisible(true);
        } catch (FileNotFoundException e) {
            System.out.println("FISIERUL NU EXISTA");
        }

    }

}
