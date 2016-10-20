package util;

/**
 * Note: Generalized version of gamut DoubleTensor clas
 */

/**
 * Stores a matrix with arbitrary dimensions of the given element type
 */

public class GenericTensor<T> {

  private int numDimensions;
  private int[] dimensionSize;
  private int size;

  // creation of generic arrays is not possible
  // sidestep this by allowing the data structure to store anything, but
  // controlling the access by typing the insert/extract methods
  private T[] values;

  /**
   * Create an empty tensor
   */
  public GenericTensor() {
    numDimensions = 0;
    dimensionSize = null;
    size = 0;
    values = null;
  }

  /**
   * Constructor
   *
   * @param dimSize an array holding the size of the tensor
   * in each dimension.
   */
  public GenericTensor(int[] dimSize) {
    init(dimSize);
  }

  /**
   * Initialize the data structure
   * This will clear all existing data
   */
  public void init(int[] dimSize) {
    numDimensions = dimSize.length;
    dimensionSize = new int[numDimensions];
    size = 1;

    for (int i = 0; i < numDimensions; i++) {
      dimensionSize[i] = dimSize[i];
      size *= dimensionSize[i];
    }

    values = (T[])new Object[size];
  }

  /**
   * Translate an array of indices in the tensor into
   * an index of the underlying values array.  Assumes
   * that each index in the array is between 1 and the size
   * of that dimension.
   */
  private int translateIndices(int[] indices) {
    int valueIndex = indices[0] - 1;

    for (int i = 1; i < numDimensions; i++) {
      valueIndex *= dimensionSize[i];
      valueIndex += indices[i] - 1;
    }

    return valueIndex;
  }

  /**
   * Set the value stored at the indexed spot in the tensor
   */
  public void setValue(T value, int[] indices) {
    int valueIndex = translateIndices(indices);
    values[valueIndex] = value;
  }

  /**
   * Set a value by directly specifying the index
   */
  public void setValue(T value, int index) {
    values[index] = value;
  }

  /**
   * Get the value stored at the indexed spot in the tensor
   */
  public T getValue(int[] indices) {
    int valueIndex = translateIndices(indices);
    return values[valueIndex];
  }

  /**
   * Access a tensor element by directly specifying the index
   */
  public T getValue(int index) {
    return values[index];
  }

  public int size() {
    return size;
  }

  public int getNumDimensions() {
    return numDimensions;
  }

  public int getSizeOfDim(int dim) {
    return dimensionSize[dim];
  }

  public int[] getSizeOfDim()  {
    return dimensionSize;
  }
}
