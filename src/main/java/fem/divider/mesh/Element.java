/*
 * Element.java
 *
 * Created on Monday, 28, March 2005, 18:26
 */

package fem.divider.mesh;

import fem.common.IFemSettings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Iterator;

import fem.geometry.Dot;
import fem.geometry.Triangle;

/**
 *
 * @author  gefox
 * @version 
 */
public class Element extends Triangle {

		/** Creates new Element
		 * and adds it to mesh of given node
		 */
    public Element(Node A_, Node B_, Node C_) {
				mesh = A_.mesh;
				getNodes()[0]=A_; getNodes()[1]=B_; getNodes()[2]=C_;
				A_.add(this);
				B_.add(this);
				C_.add(this);
				mesh.elements.add(this);
    }

    public void draw(Graphics2D g)
    {
                    Node A = getNodes()[0];
                    Node B = getNodes()[1];
                    Node C = getNodes()[2];
                    MeshPanel p = mesh.panel;
                    g.drawLine( p.xsi(A.getX()), p.ysi(A.getY()), p.xsi(B.getX()), p.ysi(B.getY()));
                    g.drawLine( p.xsi(B.getX()), p.ysi(B.getY()), p.xsi(C.getX()), p.ysi(C.getY()));
                    g.drawLine( p.xsi(C.getX()), p.ysi(C.getY()), p.xsi(A.getX()), p.ysi(A.getY()));

//				g.drawString(mesh.elements.indexOf(this)+"" , p.xsi( centralDot().x ), p.ysi( centralDot().y  ));
                    if(fem.divider.Divider.getDivider().getPreferences().isShowMeshElementNumbers())
                            {
                                    Color col = g.getColor();
                                    g.setPaint(elementNumberColor);
                                    g.drawString((index+1)+"" , p.xsi( getCentralDot().getX() ), p.ysi( getCentralDot().getY()  )); //$NON-NLS-1$
                                    g.setPaint(col);
                            }

/*
                    g.drawLine( p.xsi(centralDot().getX())-5, p.ysi(centralDot().getY())-5, 
                                    p.xsi(centralDot().getX())+5, p.ysi(centralDot().getY())+5);
                    g.drawLine( p.xsi(centralDot().getX())-5, p.ysi(centralDot().getY())+5, 
                                    p.xsi(centralDot().getX())+5, p.ysi(centralDot().getY())-5);
*/
//				drawFignu(g);
    }

    /**
     *Removes all known links to this element
     */
    public void delete()
    {
                    mesh.forget(this);
//				A.forget(this);
//				B.forget(this);
//				C.forget(this);
                    getNodes()[0].forget(this);
                    getNodes()[1].forget(this);
                    getNodes()[2].forget(this);

                    valid = false;
    }

    /**
     *Removes all known links to this element, 
     *except those that are in given node
     */
    public void deleteBy(Node node)
    {
                    mesh.forget(this);
                    if(node!=getNodes()[0])
                                    getNodes()[0].forget(this);
                    if(node!=getNodes()[1])
                                    getNodes()[1].forget(this);
                    if(node!=getNodes()[2])
                                    getNodes()[2].forget(this);

                    valid = false;
    }

//		public void replace

    public boolean hasNode(Node node)
    {
                    for(int i=0;i<3;i++)
                    {
                                    if(node==getNodes()[i]) return true;
                    }
                    return false;
    }

    public boolean isReallyInside(fem.geometry.Dot dot)
    {
                    if(!isInside(dot)) return false;
                    if( getNodes()[0].angle(dot, getNodes()[1]) < INSIDE_ANGLE) return false;
                    if( getNodes()[1].angle(dot, getNodes()[2]) < INSIDE_ANGLE) return false;
                    if( getNodes()[2].angle(dot, getNodes()[0]) < INSIDE_ANGLE) return false;
                    return true;
    }


    /**
     *@returns on success element that borders with this and is opposite to node,
     *on failure null
     */
    public Element oppositeOf(Node node)
    {
                    Node node1;
                    Node node2;
                    if(getNodes()[0]!=node)
                    {
                                    node1=getNodes()[0];
                                    if(getNodes()[1]!=node)
                                                    node2=getNodes()[1];
                                    else
                                                    node2=getNodes()[2];//if getNodes()[1]==node
                    }
                    else //getNodes()[0]==node
                    {
                                    node1=getNodes()[1];
                                    node2=getNodes()[2];
                    }

                    Element el;
                    for(Iterator i=node1.elements.iterator(); i.hasNext(); )
                    {
                                    el = (Element)i.next();
                                    if(el==this) continue;
//						if(el.hasNode(node2)&&el.valid)
                                    if(el.hasNode(node2))
                                                    return el;
                    }
                    return null;
    }

    /**
     *    /_\   =>  /|\
     *   \ /           \|/
     *@returns is it allowed 
     *  /\
     *  \/
     *  or
     *  //
     *  \\
     */
    public boolean swapDiagonalWith(Element el)
    {
                    Node a,b,//common nodes
                    c1, c2;//individual nodes
                    if(!el.hasNode(getNodes()[0]))
                    {
                                    c1 = getNodes()[0];
                                    a = getNodes()[1];
                                    b = getNodes()[2];
                    }
                    else
                                    if(!el.hasNode(getNodes()[1]))
                                    {
                                                    c1 = getNodes()[1];
                                                    a = getNodes()[2];
                                                    b = getNodes()[0];
                                    }
                                    else
                                    {
                                                    c1 = getNodes()[2];
                                                    a = getNodes()[0];
                                                    b = getNodes()[1];
                                    }
                    if(!hasNode(el.getNodes()[0]))
                                    c2 = el.getNodes()[0];
                    else
                                    if(!hasNode(el.getNodes()[1]))
                                                    c2 = el.getNodes()[1];
                                    else
                                                    c2 = el.getNodes()[2];

                    double angle = 0;
                    //c2 a c1 b
                    angle+=a.angle(c1, c2);
                    angle+=c1.angle(a, b);
                    angle+=b.angle(c1, c2);
                    angle+=c2.angle(b,a);
                    if(angle<2*Math.PI-IFemSettings.GENERAL_ACCURACY)
                    {
                                    return false;
                    }
                    //replace nodes
                    getNodes()[0]=c1;
                    getNodes()[1]=a;
                    getNodes()[2]=c2;

                    el.getNodes()[0]=c2;
                    el.getNodes()[1]=b;
                    el.getNodes()[2]=c1;

                    //update element lists in nodes
                    //ex-this
                    c1.add(el);
                    a.forget(el);
                    b.forget(this);
                    //ex-el
                    c2.add(this);
                    return true;
    }

    private void drawFignu(Graphics2D g)
    {
                    MeshPanel p = mesh.panel;
                    double A1, A2, B1, B2, C1, C2;
                    //line 1 coefficients
                    A1=getNodes()[1].getX()-getNodes()[0].getX();
                    B1=getNodes()[1].getY()-getNodes()[0].getY();
                    C1=A1*(getNodes()[1].getX()+getNodes()[0].getX())/2+B1*(getNodes()[1].getY()+getNodes()[0].getY())/2;
                    //line 2 coefficients
                    A2=getNodes()[2].getX()-getNodes()[0].getX();
                    B2=getNodes()[2].getY()-getNodes()[0].getY();
                    C2=A2*(getNodes()[2].getX()+getNodes()[0].getX())/2+B2*(getNodes()[2].getY()+getNodes()[0].getY())/2;

                    //determinants
                    double d = A1*B2-B1*A2;
                    double dx = C1*B2-B1*C2;
                    double dy = A1*C2-C1*A2;

                    double x = dx/d;
                    double y = dy/d;

                    double x1, y1, x2, y2;
                    x1=-100;
                    y1 = (-A1*x1+C1)/B1;
                    x2=100;
                    y2 = (-A1*x2+C1)/B1;
                    Paint paint = g.getPaint();
                    g.setPaint(Color.yellow);
                    g.drawLine( p.xsi(x1), p.ysi(y1), p.xsi(x2), p.ysi(y2));
                    x1=-100;
                    y1 = (-A1*x1+C1)/B1;
                    x2=100;
                    y2 = (-A1*x2+C1)/B1;
                    g.drawLine( p.xsi(x1), p.ysi(y1), p.xsi(x2), p.ysi(y2));
                    g.setPaint(paint);
    }


    private boolean isGood()
    {
                    double s = getArea();
                    if(s>mesh.settings.maxArea) return false; //too big
                    if(s<mesh.settings.minArea) return true; //too small to care about :-)
                    double a = Math.min( Math.min(getAngleValue(0), getAngleValue(1)), getAngleValue(2));
                    if(a<mesh.settings.getMinAngle()) return false; //has too small angle
                    return true; // nothing specital found
    }


    /**
     * Get third node of element or null 
     * 
     * @param n1
     * @param n2
     * @return
     */
    private Node getThirdNode(Node n1, Node n2)
    {
                    for(int i=0; i<3; i++)
                    {
                                    if(getNodes()[i]!=n1&&getNodes()[i]!=n2)
                                                    return getNodes()[i];
                    }
                    return null;
    }

//		static int i =0;

    /**
     * Split element info smaller elements if needed
     * @returns two-dimension array: result[0] has removed elements, 
     * result[1] has added elements 
     * If these was no upgrade returns zero-size array
     */
    public boolean upgrade()
    {
//				System.out.println("shrink= "+areaShrink());
                    //is upgrade needed?
                    if(valid && ((getArea() < mesh.settings.maxArea*areaShrink() && 
//				if((area() < mesh.settings.maxArea && 
//						angle(minAngle()) > mesh.settings.maxAngle
                                    getAngleValue(getMinAngleIndex()) > mesh.settings.getMinAngle()
                                    ) ||
                                    getArea() < mesh.settings.minArea) )
                                    return false; //area OK


/*				if( getNodes()[0].distance(circleCenter()) < mesh.settings.maxRadius)
                                    return false;
*/				
                    if(!isInside(circleCenter())||
                                    getAngleValue(getMinAngleIndex()) < OUTER_UPGRADE_ANGLE
                    )
                                    return outerUpgrade();
                    else
                                    return innerUpgrade();
                    //return true;
    }

    /**
     * New node inside the element
     */
    private boolean innerUpgrade()
    {
                    Dot cdot = getCentralDot();
                    Node cnode = new Node(mesh, cdot);

                    Triangle el1 = new Element(getNodes()[0], getNodes()[1], cnode);
                    Triangle el2 = new Element(getNodes()[1], getNodes()[2], cnode);
                    Triangle el3 = new Element(getNodes()[2], getNodes()[0], cnode);
                    this.delete();

                    cnode.lawson();

                    return true;
    }

    /**
     * New node on element border
     */
    private boolean outerUpgrade()
    {
                    int man = getMaxAngleIndex(); //maxAngle node
                    Node maxN = getNodes()[man];

                    int n1 = getOtherCorner1Index(man);
                    int n2 = getOtherCorner2Index(man);
                    Node N1 = getNodes()[n1];
                    Node N2 = getNodes()[n2];

                    //opposite element
                    Element op4 = this.oppositeOf(getNodes()[man]);
                    if(op4!=null)
                    {
                            int op4max = op4.getMaxAngleIndex(); Node op4Nmax = op4.getNodes()[op4max];
                            int op4_1 =  op4.getOtherCorner1Index(op4max); Node op4N1 = op4.getNodes()[op4_1];
                            int op4_2 = op4.getOtherCorner2Index(op4max); Node op4N2 = op4.getNodes()[op4_2];
                            //check if we won't make the opposite element worse
                            if( op4Nmax.angle(op4N1, op4N2)> (3*Math.PI/4) && 
                                    (op4Nmax == N1 || op4Nmax == N2) ) return false; //not upgraded 
                    }

                    Node newNode = new Node(getNodes()[n1], getNodes()[n2], 0.5,  op4==null?true:false ); 

                    Triangle el1 = new Element(newNode, getNodes()[n2], getNodes()[man]);
                    Triangle el2 = new Element(newNode, getNodes()[man], getNodes()[n1]);

                    this.delete();

                    if(op4!=null) //we have opposite element, let's split it too
                    {
                                    Node op4node = op4.getThirdNode(getNodes()[n1], getNodes()[n2]);

                                    Triangle op4el1 = new Element(op4node, getNodes()[n2], newNode);
                                    Triangle op4el2 = new Element(newNode, getNodes()[n1], op4node);

                                    op4.delete();
//						op4node.lawson();

                    }

//				maxN.lawson();

                    newNode.lawson();
                    return true;
    }

    /**
     *Returns coeficient (0...1] , that tells maximal allowed size
     *of this element (relatively to mesh.settings.maxArea)
     */
    private double areaShrink()
    {
                    double leastOriginalAngle=Math.PI; //180 degres
                    for(int i=0; i<2; i++)
                    {
                                    if(getNodes()[i].original)
                                    {
                                                    if(getNodes()[i].originalAngle < leastOriginalAngle)
                                                                    leastOriginalAngle=getNodes()[i].originalAngle;
                                    }
                    }

                    return leastOriginalAngle/Math.PI;
    }

    public int getIndex() {
            return index;
    }

    void setIndex(int i) {
            index = i;
    }

    public void setNodes(Node[] nodes)
    {
            setCorners(nodes);
    }

    @Override
    protected Dot[] createCornersArray() {
        return new Node[3];
    }
		
                
                
    public Node[] getNodes()
    {
            return (Node[])getCorners();
    }
		
    private int index; //index in array of elements in mesh
    private Mesh mesh;
    private boolean edge = false;
    private boolean valid = true;

    public static double INSIDE_ANGLE = Math.toRadians(1);
    public static double OUTER_UPGRADE_ANGLE = Math.toRadians(20);

    public static Color elementNumberColor=new Color(0, 100, 0);
}//end class