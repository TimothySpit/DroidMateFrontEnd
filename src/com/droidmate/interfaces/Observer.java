package com.droidmate.interfaces;

interface Observer<T>
{
  public void update( Observable<T> o, T arg );
}