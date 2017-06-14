/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.engine;
/**
 *
 * @author Phuoc
 */
public enum Alliance 
{
    WHITE
    {
        @Override
        public int getDirection()
        {
            return -1;
        }
        
        @Override 
        public int getOppositeDirection()
        {
            return 1;
        }
        
        @Override
        public boolean isWhite()
        {
            return true;
        }
        
        @Override
        public boolean isBlack()
        {
            return false;
        }
        
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,final BlackPlayer blackPlayer)
        {
            return whitePlayer;
        }
        
        @Override
        public boolean isPawnPromotionSquare(int position)
        {
            return BoardUtils.FIRST_ROW[position];
        }
    },   
    BLACK
    {
        @Override
        public int getDirection()
        {
            return 1;
        }
        
        @Override 
        public int getOppositeDirection()
        {
            return -11;
        }
        
        @Override
        public boolean isWhite()
        {
            return false;
        }
        
        @Override
        public boolean isBlack()
        {
            return true;
        }
        
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,final BlackPlayer blackPlayer)
        {
            return blackPlayer;
        }
        
        @Override
        public boolean isPawnPromotionSquare(int position)
        {
            return BoardUtils.EIGHTH_ROW[position];
        }       
    };
    
    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract int getOppositeDirection(); 
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);
    public abstract Player choosePlayer(WhitePlayer whitePlayer,BlackPlayer blackPlayer);
}
