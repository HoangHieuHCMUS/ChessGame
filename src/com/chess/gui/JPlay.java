/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.gui;

import com.chess.engine.*;
import com.chess.engine.Move.MoveFactory;
import com.google.common.collect.Lists;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Phuoc
 */

//Playing GUI
public class JPlay extends javax.swing.JFrame {
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private final MoveLog moveLog;
    private Piece humanMovedPiece;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private static final String defaultPieceImagesPath = "src/icon/"; 
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    /**
     * Creates new form JPlay
     */
    public JPlay() {
        initComponents();
        this.setLayout(new BorderLayout());
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        //this.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.add(this.boardPanel,BorderLayout.CENTER);
    }

    private class BoardPanel extends JPanel
    {
        final List<TilePanel> boardTiles;
        
        BoardPanel()
        {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++)    
            {
                final TilePanel tilePanel = new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.decode("#8B4726"));
            validate();
        }
        
        public void drawBoard (final Board board)
        {
            removeAll();
            for (final TilePanel tilePanel : boardTiles)
            {
                tilePanel.drawTile(board); 
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }
    
    public static class MoveLog
    {
        private final List<Move> moves;
        
        MoveLog()
        {
            this.moves = new ArrayList<>();
        }    
        
        public List<Move> getMoves()
        {
            return this.moves;
        }
        
        public void addMove(final Move move)
        {
            this.moves.add(move);
        }
        
        public int size()
        {
            return this.moves.size();
        }
        
        public void clear()
        {
            this.moves.clear();
        }
        
        public Move removeMove(int index)
        {
            return this.moves.remove(index);
        }
        
        public boolean removeMove(final Move move)
        {
            return this.moves.remove(move);
        }
    }
    
    private class TilePanel extends JPanel
    {
        private final int tileID;
        
        TilePanel(final BoardPanel boardPanel,final int tileID)
        {
            super(new GridBagLayout()); 
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            
            addMouseListener(new MouseListener()
            {
                @Override
                public void mouseClicked(final MouseEvent e)
                {
                    if (SwingUtilities.isRightMouseButton(e)) 
                    {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } 
                    else if (SwingUtilities.isLeftMouseButton(e)) 
                    {
                        if (sourceTile == null) 
                        {
                            sourceTile = chessBoard.getTile(tileID);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) 
                                sourceTile = null;
                        } 
                        else 
                        {
                            destinationTile = chessBoard.getTile(tileID);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) 
                            {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(MouseEvent e) {
                   
                }
            });
            validate();
        }
        
        private void highlightLegals(final Board board) 
        {
            for (final Move move : pieceLegalMoves(board)) 
            {
                if (move.getDestinationCoordinate() == this.tileID) 
                {
                    try {
                        add(new JLabel(new ImageIcon(ImageIO.read(new File("src/icon/green_dot.png")))));
                    }
                    catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
         private Collection<Move> pieceLegalMoves(final Board board) 
         {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) 
            {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
        
        public void drawTile(final Board board)
        {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
        
        private void assignTilePieceIcon(final Board board)
        {
            this.removeAll();
            if (board.getTile(this.tileID).isTileOccupied())
            {
                try
                {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileID).getPiece().getPieceAlliance().toString().substring(0, 1) +
                          board.getTile(this.tileID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                }
                catch (IOException e)
                {    
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.FIRST_ROW[this.tileID] ||
                BoardUtils.THIRD_ROW[this.tileID] ||
                BoardUtils.FIFTH_ROW[this.tileID] ||
                BoardUtils.SEVENTH_ROW[this.tileID]) 
            {
                setBackground(this.tileID % 2 != 0 ? lightTileColor : darkTileColor);
            } 
            else if(BoardUtils.SECOND_ROW[this.tileID] ||
                    BoardUtils.FOURTH_ROW[this.tileID] ||
                    BoardUtils.SIXTH_ROW[this.tileID]  ||
                    BoardUtils.EIGHTH_ROW[this.tileID]) 
            {
                setBackground(this.tileID % 2 == 0 ? lightTileColor : darkTileColor);
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JPlay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JPlay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JPlay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JPlay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JPlay().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
