package main;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.*;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.*;

public class StartDemo extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1605673331356026328L;
	private static final Dimension d = new Dimension(1024,768);
	private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

	
	public StartDemo(){
		buildGUI();
		drawGraph();
		
	}
	
	private void buildGUI() {
		GUI();
	}
	
	private void GUI(){
		this.setTitle("JGraphT Adapter to JGraphX Demo");
		this.setLayout(new BorderLayout());
		this.setPreferredSize(d);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	StartDemo dm = new StartDemo();
            }
        });
	}
	
	public void drawGraph(){
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
        
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);
        
        
        VertexFactory<String> vertexFactory = new VertexFactory<String>(){
            int n = 0;
            @Override
            public String createVertex()
            {
                String s = String.valueOf(n);
                n++;
                return s;
            }
        };
        
        //ScaleFreeGraphGenerator<String, DefaultEdge> sf = new ScaleFreeGraphGenerator<String, DefaultEdge>(50);
        //sf.generateGraph(g, vertexFactory, null);
        
        WattsStrogatzGraphGenerator<String, DefaultEdge> ws = new WattsStrogatzGraphGenerator<String, DefaultEdge>(50, 6, 0.2);
        ws.generateGraph(g, vertexFactory, null);
        
        JPanel gp = new JPanel();
        gp.setLayout(new BorderLayout());
        gp.setPreferredSize(this.getContentPane().getSize());
        gp.add(new mxGraphComponent(jgxAdapter));
        this.add(gp, BorderLayout.CENTER);
        this.setSize(d);
        
        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
        
	}

}
