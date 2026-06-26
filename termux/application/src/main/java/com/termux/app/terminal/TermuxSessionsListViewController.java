/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.app.terminal;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.LayoutRes;

import com.termux.app.TermuxService;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.List;

/**
 * Adapter for the terminal session list in the drawer.
 * Adapted from AndroidIDE's TermuxSessionsListViewController.
 */
public class TermuxSessionsListViewController extends ArrayAdapter<TermuxSession>
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final android.app.Activity mActivity;
    private final TermuxTerminalSessionActivityClient mSessionClient;
    private final int mLayoutRes;
    private final int mTitleViewId;

    final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
    final StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);

    public TermuxSessionsListViewController(
            android.app.Activity activity,
            TermuxTerminalSessionActivityClient sessionClient,
            @LayoutRes int layoutRes,
            int titleViewId,
            List<TermuxSession> sessionList
    ) {
        super(activity.getApplicationContext(), layoutRes, sessionList);
        this.mActivity = activity;
        this.mSessionClient = sessionClient;
        this.mLayoutRes = layoutRes;
        this.mTitleViewId = titleViewId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View sessionRowView = convertView;
        if (sessionRowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            sessionRowView = inflater.inflate(mLayoutRes, parent, false);
        }

        TextView sessionTitleView = sessionRowView.findViewById(mTitleViewId);

        TermuxSession termuxSession = getItem(position);
        if (termuxSession == null) {
            sessionTitleView.setText("null session");
            return sessionRowView;
        }

        TerminalSession sessionAtRow = termuxSession.getTerminalSession();
        if (sessionAtRow == null) {
            sessionTitleView.setText("null session");
            return sessionRowView;
        }

        String name = sessionAtRow.mSessionName;
        String sessionTitle = sessionAtRow.getTitle();

        String numberPart = "[" + (position + 1) + "] ";
        String sessionNamePart = TextUtils.isEmpty(name) ? "" : name;
        String sessionTitlePart = TextUtils.isEmpty(sessionTitle) ? ""
            : ((sessionNamePart.isEmpty() ? "" : "\n") + sessionTitle);

        String fullSessionTitle = numberPart + sessionNamePart + sessionTitlePart;
        SpannableString fullSessionTitleStyled = new SpannableString(fullSessionTitle);
        fullSessionTitleStyled.setSpan(boldSpan, 0, numberPart.length() + sessionNamePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fullSessionTitleStyled.setSpan(italicSpan, numberPart.length() + sessionNamePart.length(), fullSessionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sessionTitleView.setText(fullSessionTitleStyled);

        boolean sessionRunning = sessionAtRow.isRunning();
        if (sessionRunning) {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        int color = sessionRunning || sessionAtRow.getExitStatus() == 0 ? Color.WHITE : Color.RED;
        sessionTitleView.setTextColor(color);

        return sessionRowView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TermuxSession clickedSession = getItem(position);
        if (clickedSession != null) {
            mSessionClient.setCurrentSession(clickedSession.getTerminalSession());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }
}
