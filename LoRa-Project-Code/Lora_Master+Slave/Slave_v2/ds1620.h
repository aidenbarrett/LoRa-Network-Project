/*
  DS160 Library
 
  by Ruben Laguna 2009 <ruben.laguna at gmail.com>
  based on examples from Tom Tigoe <http://www.arduino.cc/en/Reference/SoftwareSerialExample>
  and phanderson <http://www.phanderson.com/printer/ds1620/ds1620.html>
  
  
  written: 30 Aug 2008
 
 
*/
#ifndef ds1620_h
#define ds1620_h

#include <Arduino.h> 

class Ds1620
{
  public:
    Ds1620(int rst, int clk, int dq);
    void config();
    void start_conv() ;
    void stop_conv() ;
    int read_data();
  
  private:
    int rstPin, clkPin, dqPin; 
    int read_raw_data(void);
    void write_command(int command);
    void out_bit(int bit) ;
    void clk_high(void);
    void clk_low(void);
    void rst_high(void);
    void rst_low(void);
  
};



#endif
