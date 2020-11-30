/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LAKillah
 */
package src.view ;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

import src.socket.ClientSocket;
import src.util.DataGame;
import src.util.TagName;

public class gameDoMin extends JFrame implements ActionListener,  KeyListener, Runnable {
    
    /**
	 * 
	 */
	private ClientSocket client;
	private JSONObject dataRound;
    int BOM, dem = 0;
    int maxXY = 1000;
    boolean die = false;
    Timer timer;
    boolean flag = false;
    private Color bom_cl = Color.red;
    int key_flag =  KeyEvent.VK_H;
    private Color background_number_cl = Color.yellow;
    private Color background_null_cl = Color.gray;
    private Color flag_cl = Color.green;
    private int m, n;
    int M[] = { 8, 15, 21 };
    int N[] = { 10, 19, 27 };
    int Mines[] = { 10, 40, 100 };
    private int values[][] = new int[maxXY][maxXY];
    private JButton bt[][] = new JButton[maxXY][maxXY];
    private JLabel point_lb, hightPoint_lb, temp_lb, flag_lb;
    private boolean tick[][] = new boolean[maxXY][maxXY];
    private JButton mines_bt;
    private JPanel pn0, pn, pn2;
    Container cn;      	

    @Override
    public void run() {
    	
    	
    }
    
    public gameDoMin( String s, int k, int[][] data, ClientSocket client, JSONObject dataRound) {
        super(s);
        this.client = client;
        this.dataRound = dataRound;
        BOM = Mines[k];
        m = M[k];
        n = N[k];

       for(int i = 0; i< maxXY ; i++){
    	   for(int j = 0; j< maxXY ; j++){
        	   values[i][j] = 0;
           }
       }
       
       for(int i = 1; i< 9 ; i++){
    	   for(int j = 1; j< 11 ; j++){
        	   values[i][j] = data[i-1][j-1];
           }
       }
       
        cn = this.getContentPane();
        pn = new JPanel();
        pn.setLayout(new GridLayout(m, n));
        for (int i = 0; i <= m + 1; i++)
        for (int j = 0; j <= n + 1; j++) {
            bt[i][j] = new JButton("   ");
            tick[i][j] = true;
        }
        for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++) {
            bt[i][j] = new JButton("   ");
            pn.add(bt[i][j]);
            bt[i][j].setActionCommand(i + " " + j);
            bt[i][j].addActionListener(this);
            bt[i][j].addKeyListener(this);
        }
        point_lb = new JLabel("00:00:00:00");
        point_lb.setFont(new Font("Arial", 1, 20));
        hightPoint_lb = new JLabel(hightPoint(k));
        hightPoint_lb.setFont(new Font("Arial", 1, 20));
        temp_lb = new JLabel("     ");
        mines_bt = new JButton(String.valueOf(BOM));
        mines_bt.setBackground(flag_cl);
        flag_lb = new JLabel("Đang gỡ bom (h)");
        flag_lb.setForeground(bom_cl);
        flag_lb.setFont(new Font("Arial", 1, 15));
        pn2 = new JPanel();
        pn2.setLayout(new FlowLayout());
        pn2.add(mines_bt);
        pn2.add(flag_lb);
        pn0 = new JPanel();
        pn0.setLayout(new FlowLayout());
        pn0.add(point_lb);
        pn0.add(temp_lb);
        pn0.add(hightPoint_lb);
        cn.add(pn0, "North");
        cn.add(pn);
        cn.add(pn2, "South");
        this.setVisible(true);
        this.pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                point_lb.setText(next(point_lb));
            }
        });
    }

   public void setValues(int[][] data){
       this.values = data;
   }

    public String next(JLabel lb) {
        String str[] = lb.getText().split(":");
        int tt = Integer.parseInt(str[3]);
        int s = Integer.parseInt(str[2]);
        int m = Integer.parseInt(str[1]);
        int h = Integer.parseInt(str[0]);
        String kq = "";
        int sum = tt + s * 100 + m * 60 * 100 + h * 60 * 60 * 100 + 1;
        if (sum % 100 > 9)
                kq = ":" + sum % 100 + kq;
        else
                kq = ":0" + sum % 100 + kq;
        sum /= 100;

        if (sum % 60 > 9)
                kq = ":" + sum % 60 + kq;
        else
                kq = ":0" + sum % 60 + kq;
        sum /= 60;

        if (sum % 60 > 9)
                kq = ":" + sum % 60 + kq;
        else
                kq = ":0" + sum % 60 + kq;
        sum /= 60;
        if (sum > 9)
                kq = sum + kq;
        else
                kq = "0" + sum + kq;
        return kq;
    }

    public void open(int i, int j) {
        if (tick[i][j] && values[i][j] != -1) {
            bt[i][j].setText(String.valueOf(values[i][j]));
            bt[i][j].setBackground(background_number_cl);
            tick[i][j] = false;
            checkWin();
        }
    }

    public void openEmpty(int i, int j) {
        if (tick[i][j]) {
            tick[i][j] = false;
            bt[i][j].setBackground(background_null_cl);
            checkWin();
            for (int h = i - 1; h <= i + 1; h++)
            for (int k = j - 1; k <= j + 1; k++)
            if (h >= 0 && h <= m && k >= 0 && k <= n) {
                if (values[h][k] == 0 && tick[h][k])
                    openEmpty(h, k);
                else
                    open(h, k);
            }
        }
    }

    public void addFlag(int i, int j) {
            if (bt[i][j].getBackground() == flag_cl) {
                    tick[i][j] = true;
                    bt[i][j].setBackground(null);
                    bt[i][j].setIcon(null);
                    mines_bt.setText(String.valueOf(Integer.parseInt(mines_bt.getText()) + 1));
            } else if (tick[i][j] && Integer.parseInt(mines_bt.getText()) > 0) {
                    tick[i][j] = false;
                    bt[i][j].setBackground(flag_cl);

            }
            checkWin();
    }

    public void checkWin() {
        for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
        if (tick[i][j]) {
                return;
        }
        int k = 0;
        for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
        if (bt[i][j].getBackground() == flag_cl && values[i][j] != -1)
            return;
        else if (bt[i][j].getBackground() == flag_cl)
            k++;
        if (k <= BOM) {
            timer.stop();
            try {
                checkPoint(point_lb.getText(), 10);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int s = 12;
            die = true;
            JOptionPane.showMessageDialog(null, "Bạn đã chiến thắng!");
            
            this.dataRound.put("tag", TagName.getCompleteGame());
            this.dataRound.remove("datagame");
            this.client.complete_defeat(this.dataRound.toString());
            
            nextRound();
        }
    }

    public void checkPoint(String s, int k) throws IOException {
        String file = "point.txt";
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String str[] = { "", "", "" };
        for (int i = 0; i <= 2; i++)
            str[i] = br.readLine();
        fr.close();
        if (s.compareTo(str[k]) < 0) {
            str[k] = s;
            FileWriter f = new FileWriter(file);
            f.write(str[0] + "\n");
            f.write(str[1] + "\n");
            f.write(str[2] + "\n");
            f.flush();
            f.close();
        }
    }

    String hightPoint(int k) {
        String file = "point.txt";
        String str = "";
        FileReader fr;
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            try {
                for (int i = 0; i <= k; i++)
                        str = br.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            String maxTime = "99:99:99:99";
            // TODO Auto-generated catch block
            FileWriter f;
            try {
                f = new FileWriter(file);
                f.write(maxTime + "\n");
                f.write(maxTime + "\n");
                f.write(maxTime + "\n");
                f.flush();
                f.close();
                return "99:99:99:99";
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return str;
    }

    public void loss() {
        for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
        if (values[i][j] == -1) {
            bt[i][j].setBackground(bom_cl);
            bt[i][j].setText("X");
        }
    }
    
    public void nextRound(){
    	int result = JOptionPane.showConfirmDialog(this, "Bạn muốn chơi lại không ??");
    	if(result == JOptionPane.YES_OPTION){
    		this.client.rematchGame(this.dataRound.get("idCompetitor").toString());
    		dispose();
    	}
    	else{
    		dispose();
    	}
    }
    
    

    public void actionPerformed(ActionEvent e) {
         if (!die) {
            timer.start();
            int i = 0, j = 0;
            String s = e.getActionCommand();
            int k = s.indexOf(32);
            i = Integer.parseInt(s.substring(0, k));
            j = Integer.parseInt(s.substring(k + 1, s.length()));
            if (!flag) {
                if (values[i][j] == -1) {
                    if (bt[i][j].getBackground() != flag_cl) {
                        bt[i][j].setBackground(bom_cl);
                        bt[i][j].setText("X");
                        timer.stop();
                        loss();
                        die = true;
                        
                        this.dataRound.put("tag", TagName.getDefeatGame());
                        this.dataRound.remove("datagame");
                        this.client.complete_defeat(this.dataRound.toString());
                        
                        nextRound();
                    }
                } else if (values[i][j] == 0) {
                    openEmpty(i, j);
                } else {
                    open(i, j);
                }
            } else {
                addFlag(i, j);
            }
        }
    }


    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()== key_flag) {
            flag = !flag;
        }
        if (flag) {
            flag_lb.setText("Đang cắm cờ (h)");
            flag_lb.setForeground(flag_cl);
        } else {
            flag_lb.setText("Đang gỡ bom (h)");
            flag_lb.setForeground(bom_cl);
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
    
}
