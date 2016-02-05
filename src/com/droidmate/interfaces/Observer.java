package com.droidmate.interfaces;

/**
 * Interface for classes which observe an observable.
 *
 * @param <T>
 */
public interface Observer<T>
{
  public void update( Observable<T> o, T arg );
}