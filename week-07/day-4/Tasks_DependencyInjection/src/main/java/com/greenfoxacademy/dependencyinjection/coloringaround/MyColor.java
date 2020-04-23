package com.greenfoxacademy.dependencyinjection.coloringaround;

// # Playing around with colors

// There is one interface given:

import com.greenfoxacademy.dependencyinjection.hellodiproject.Printer;

public interface MyColor {
  public void printColor(Printer printer);
}

// Any kind of color class (for i.e. RedColor) can implement this interface overriding `printColor();` sout-ing for i.e. `"It is red in color..."`.

// - Implement the interface multiple times for different colors
// - Annotate one of them with `@Service` and autowire the interface in the application and use the `printColor()` method
// - Move the annotation to another implementation
// - Change the implementations of the printColor() method to use the `Printer` service with autowiring (instead of sout-ing)