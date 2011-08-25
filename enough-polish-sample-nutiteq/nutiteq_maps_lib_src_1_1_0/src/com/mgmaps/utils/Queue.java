package com.mgmaps.utils;

public class Queue {
  protected int count;
  protected int size;
  protected Object[] elements;
  public static final int DEFAULT_SIZE = 10;

  /**
   * Constructor for Queue.
   */
  public Queue(int size) {
    this.size = size;
    this.elements = new Object[size];
    this.count = 0;
  }

  public Queue() {
    this(DEFAULT_SIZE);
  }

  /**
   * Clear the queue - set the elements to zero.
   */
  public synchronized void clear() {
    for (int i = 0; i < count; i++) {
      elements[i] = null;
    }
    this.count = 0;
  }

  public synchronized Object[] getElements() {
    return elements;
  }

  /**
   * Insert an element into the queue.
   * 
   * @param o
   *          the object to push
   */
  public synchronized void push(Object o) {
    // if the element already exists, remove it
    remove(o);

    // check if the queue is full
    if (size == count) {
      Object[] oldElements = elements;
      size <<= 1;
      elements = new Object[size];
      System.arraycopy(oldElements, 0, elements, 0, count);
    }

    elements[count++] = o;
  }

  /**
   * Remove an element from the queue.
   * 
   * @param pos
   *          the position of the element to remove
   */
  public synchronized void remove(int pos) {
    if (pos >= count) {
      return;
    }

    // move towards the front
    for (int i = pos; i < count - 1; i++) {
      elements[i] = elements[i + 1];
    }
    elements[--count] = null;
  }

  /**
   * Remove an object from the queue.
   * 
   * @param o
   *          the object to remove
   */
  public synchronized void remove(Object o) {
    for (int i = 0; i < count; i++) {
      if (elements[i].equals(o)) {
        remove(i);
        break;
      }
    }
  }

  /**
   * Is the queue empty?
   * 
   * @return true if it is
   */
  public synchronized boolean isEmpty() {
    return count == 0;
  }

  /**
   * Get queue size.
   * 
   * @return the size of the queue
   */
  public synchronized int getSize() {
    return size;
  }

  /**
   * Get the number of elements
   * 
   * @return the number of elements
   */
  public synchronized int getCount() {
    return count;
  }

  /**
   * Get the top element
   * 
   * @return the top element
   */
  public synchronized Object top() {
    if (count == 0) {
      return null;
    }
    return elements[0];
  }

  /**
   * Pop the top element.
   * 
   * @return the top element
   */
  public synchronized Object pop() {
    Object element = top();
    remove(0);
    return element;
  }

  /**
   * Search for an object.
   */
  public synchronized Object find(Object o) {
    for (int i = 0; i < count; i++) {
      if (elements[i].equals(o)) {
        return elements[i];
      }
    }

    return null;
  }

  /**
   * Direct access to an element.
   */
  public synchronized Object get(int i) {
    return elements[i];
  }
}
