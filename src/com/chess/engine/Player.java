/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Phuoc
 */
public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;
    
    Player(final Board board,final Collection<Move> legalMoves,final Collection<Move> opponentMoves)
    {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),opponentMoves).isEmpty();
    }
    
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves)
    {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves)
        {
            if (piecePosition == move.getDestinationCoordinate())
                attackMoves.add(move);
        }
        return Collections.unmodifiableList(attackMoves);
    }
    
    public King getPlayerKing()
    {
        return this.playerKing;
    }
    
    public Collection<Move> getLegalMoves()
    {
        return this.legalMoves;
    }
    
    private King establishKing()
    {
        for (final Piece piece : getActivePieces())
        {
            if (piece.getPieceType().isKing())
                return (King) piece;
        }
        throw new RuntimeException("Should not reach here! " +this.getAlliance()+ " king could not be established!");
    }
    
    public boolean isMoveLegal(final Move move)
    {
        return this.legalMoves.contains(move);
    }
    
    //TODO
    public boolean isInCheck()
    {
        return this.isInCheck;
    }
    
    public boolean hasEscapeMove()
    {
        for (final Move move : this.legalMoves)
        {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone())
                return true;
        }
        return false;
    }
    
    public boolean isInCheckMate()
    {
        return this.isInCheck() && !hasEscapeMove();
    }
    
    public boolean isInStaleMate()
    {
        return !this.isInCheck() && !hasEscapeMove();
    }
    
    public boolean isCastled()
    {
        return false;
    }
    
    public MoveTransition makeMove(final Move move)
    {
        if (!isMoveLegal(move))
            return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition()
                                                                           ,transitionBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty())
            return new MoveTransition(this.board,move,MoveStatus.LEAVES_PLAYER_IN_CHECK);
        return new MoveTransition(transitionBoard,move,MoveStatus.DONE);
    }
    
    public abstract Collection<Piece> getActivePieces();
    public abstract Player getOpponent();
    public abstract Alliance getAlliance();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegall, Collection<Move> opponentLegals);
}
