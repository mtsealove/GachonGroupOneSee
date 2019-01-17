package kr.ac.gachon.www.GachonGroup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.ac.gachon.www.GachonGroup.modules.FirebaseHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PRFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PRFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int pageCount;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PRFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PRFragment newInstance(String param1, String param2) {
        PRFragment fragment = new PRFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    int[] buttonID=new int[8];
    Button[] buttons=new Button[8];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pageCount=Integer.parseInt(getArguments().getString("pageCount"));
        ViewGroup rootview=(ViewGroup)inflater.inflate(R.layout.fragment_pr, container,false);
        buttonID[0]=R.id.board0;
        buttonID[1]=R.id.board1;
        buttonID[2]=R.id.board2;
        buttonID[3]=R.id.board3;
        buttonID[4]=R.id.board4;
        buttonID[5]=R.id.board5;
        buttonID[6]=R.id.board6;
        buttonID[7]=R.id.board7;
        for(int i=0; i<8; i++)
            buttons[i]=(Button)rootview.findViewById(buttonID[i]);
        FirebaseHelper helper=new FirebaseHelper();
        helper.setTitleBtn(buttons, "PublicRelation", pageCount, 8);

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
