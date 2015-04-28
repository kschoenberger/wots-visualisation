package files;

/**
 * @author Moritz Horsch <horsch@cdc.informatik.tu-darmstadt.de>
 */
public abstract class Function implements XMSSObjectIdentifer {

    private final String oid;

    /**
     * Creates a new Function.
     *
     * @param oid Object identifier
     */
    protected Function(String oid) {
	this.oid = oid;
    }

    /**
     * Returns the object identifier.
     *
     * @return Object identifier.
     */
    public String getOID() {
	return oid;
    }

    /**
     * Returns the length of the function.
     *
     * @return Length
     */
    public abstract int getLength();
}
