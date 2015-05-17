/*
 * MeshPanel.java
 *
 * Created on Saturday, 19, March 2005, 12:37
 */

package fem.divider.mesh;

import java.awt.*;

/**
 *
 * @author  gefox
 */
public class MeshPanel extends fem.divider.AbstractPanel {

		/** Creates new form MeshPanel */
    public MeshPanel(fem.divider.RectangleArea world_) {
				super(world_);
//        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
		private void initComponents() {//GEN-BEGIN:initComponents
				
				setLayout(new java.awt.BorderLayout());
				
		}//GEN-END:initComponents

		/**
		 *Set mesh
		 *Intended to be used only by Mesh class
		 */

		public void setMesh(Mesh m)
		{
				mesh = m;
		}

		public Mesh getMesh()
		{
				return mesh;
		}
		
		public void setMeasure(fem.divider.mesh.measure.Measure _measure)
		{
			measure=_measure;
		}

		public fem.divider.mesh.measure.Measure  getMeasure()
		{
			return measure;
		}
		public void draw(Graphics2D g2) {
				if(mesh!=null)
						mesh.draw(g2);
				if(measure!=null)
						measure.draw(g2);
		}
		
		// Variables declaration - do not modify//GEN-BEGIN:variables
		// End of variables declaration//GEN-END:variables

				
		private Mesh mesh;
		private fem.divider.mesh.measure.Measure measure=null;
}