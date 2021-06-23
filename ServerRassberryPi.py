'''
------------------- Tata Consultancy Services
------------------- Remote Internship Project Report
* Author List: Muzammil Khan & Sayantan Saha
* Mentor: Bhavana Kalyankar
* Internship Duration: 16 Weeks
* Filename: ClientRaspberryPi.py
* Project Name: IOT for a Grocery Store 
* Functions: sendTPTW, add_item, remove_item, send_table, log_table_creator, invoice_generator, end_shopping, handle_client, start
                    
'''


import socket 
import threading
import psycopg2
import datetime
import time
import pickle

###############################
# Socket connection Configurations #
###############################
HEADER = 64
PORT = 80
SERVER = socket.gethostbyname(socket.gethostname())
ADDR = (SERVER, PORT)
FORMAT = 'utf-8'
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(ADDR)
################################

'''
* Function Name: sendTPTW
* Input: cart_no as string, conn as socket connection, db_con as database connection
* Output: None 
* Logic: Retrieves totalPrice and totalWeight values from database and sends to appropriate clients 
* Example Call: sendTPTW()
'''

def sendTPTW(cart_no,conn,db_con):
    try:
        cur = db_con.cursor()
        cur.execute("select sum(price) from %s"%(cart_no))
        totalPrice = cur.fetchone()
        if totalPrice:
            totalPriceStr = str(totalPrice[0])
        else:
            totalPriceStr = "0"
        conn.send(totalPriceStr.encode(FORMAT))
        time.sleep(0.1)
        cur.execute("select sum(weight) from %s"%(cart_no))
        totalWeight = cur.fetchone()
        if totalWeight:
            totalWeightStr = str(totalWeight[0])
        else:
            totalWeightStr = '0'
        #conn.send(totalWeightStr.encode(FORMAT))
        db_con.close()
    except:
        pass
		
'''
* Function Name: add_item
* Input: cart_no as string, p_id as string, conn as socket connection
* Output: None 
* Logic: Fetches the product details from product table and adds the product for the particular client
* Example Call: add_item(cart_no,p_id,conn)
'''

def add_item(cart_no,p_id,conn):
    try:
        con = psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
        timestamp = datetime.datetime.utcnow()
        cur = con.cursor()
        cur.execute("select * from products where product_id = '%s'"%(p_id))
        p_data = cur.fetchone()
        p_name = p_data[1]
        p_rate = p_data[2]
        p_weight = p_data[3]
        cur.execute("select quantity from %s where product_id = '%s'"%(cart_no,p_id))
        p_quantity = cur.fetchone()
        if p_quantity:
            cur.execute("update %s set quantity = quantity + 1 where product_id = '%s'"%(cart_no,p_id))
            cur.execute("update %s set weight = weight + %f where product_id = '%s'"%(cart_no,p_weight,p_id))
            cur.execute("update %s set price = price + %f where product_id = '%s'"%(cart_no,p_rate,p_id))
            con.commit()
        else:
            cur.execute("INSERT INTO %s (product_id,product_name,weight,rate,quantity,price,timestamp) VALUES ('%s','%s',%f,%f,1,%f,'%s')"%(cart_no,p_id,p_name,p_weight,p_rate,p_rate,timestamp))
            con.commit()
        sendTPTW(cart_no,conn,con)
    except: 
        pass

'''
* Function Name: remove_item
* Input: cart_no as string, p_id as string, conn as socket connection
* Output: None 
* Logic: Fetches the product details from product table and removes the product for the particular client
* Example Call: remove_item(cart_no,p_id,conn)
'''		
		
def remove_item(cart_no,p_id,conn):
    try:
        con = psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
        cur = con.cursor()
        cur.execute("select * from products where product_id = '%s'"%(p_id))
        p_data = cur.fetchone()
        p_rate = p_data[2]
        p_weight = p_data[3]
        cur.execute("select quantity from %s where product_id = '%s'"%(cart_no,p_id))
        p_quantity = cur.fetchone()
        if p_quantity:
            if(p_quantity[0] > 1):
                cur.execute("update %s set quantity = quantity - 1 where product_id = '%s'"%(cart_no,p_id))
                cur.execute("update %s set weight = weight - %f where product_id = '%s'"%(cart_no,p_weight,p_id))
                cur.execute("update %s set price = price - %f where product_id = '%s'"%(cart_no,p_rate,p_id))
                con.commit()
            elif(p_quantity[0] == 1):
                cur.execute("delete from %s where product_id = '%s'"%(cart_no,p_id))
                con.commit()
            
        sendTPTW(cart_no,conn,con)
    except:
        pass

'''
* Function Name: send_table
* Input: cart_no as string, conn as socket connection
* Output: None 
* Logic: Fetches the cart details for the particular client from database and sends them as a string to that client
* Example Call: send_table(cart_no,conn)
'''			

def send_table(cart_no,conn):
    con = psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
    cur = con.cursor()
    cur.execute("select product_name, rate, quantity, price from cart1")
    resultset = cur.fetchall()
    resultlist = []
    for row in resultset:
        s = str(row[0])
        for i in range(1,4):
            s = ' | '.join((s,str(row[i])))
        s = s + '-'
        resultlist.append(s)
    #msg = pickle.dumps(resultlist)
    #msg = bytes(f"{len(msg):<{HEADER}}", 'utf-8')+msg
    msg = listToString(resultlist)
    conn.send(msg.encode(FORMAT))
    cur.close()
    con.close()


def listToString(s):
    # initialize an empty string
    str1 = ""

    # traverse in the string
    for ele in s:
        str1 += ele

        # return string
    return str1
'''
* Function Name: log_table_creator
* Input: table as string
* Output: None 
* Logic: create a log_table as per given name as parameter
* Example Call: log_table_creator(table)
'''	
	
def log_table_creator(table):
    con = psycopg2.connect(database="invoice_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
    cur = con.cursor()    
    cur.execute("create table if not exists %s (invoice_no text, cart_no text, product_id text, product_name text, weight numeric, rate numeric, quantity integer, price numeric, timestamp time with time zone)"%(table))
    con.commit()
    con.close()

'''
* Function Name: invoice_generator
* Input: table as string
* Output: invoice_no as string 
* Logic: generates a unique invoice number
* Example Call: invoice_generator(table)
'''	

def invoice_generator(table):
    con = psycopg2.connect(database="invoice_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
    cur = con.cursor()
    date = str(datetime.datetime.now().date())
    year = date[0:4]
    month = date[5:7]
    day = date[8:10]
    cur.execute("select invoice_no from %s order by invoice_no desc limit 1"%(table))
    max_invoice_no = cur.fetchone()
    if(max_invoice_no ==None):
        invoice_no = "".join(("In",year))
        invoice_no = "".join((invoice_no,month))
        invoice_no = "".join((invoice_no,day))
        invoice_no = "".join((invoice_no,"00001"))
    else:
        invoice_no = str(max_invoice_no[0])
        digit = invoice_no[10:]
        int_digit = int(digit) + 1
        digit = (len(digit) - len(str(int_digit)))*"0" + str(int_digit)
        invoice_no = invoice_no[0:10]+digit    
    con.close()
    return invoice_no
	
'''
* Function Name: end_shopping
* Input: cart_no as string, conn as socket connection
* Output: None 
* Logic: Ends shopping for a client and store all purchase details in the log_table according to the purchase date against a unique invoice number and sends that invoice number to the client
* Example Call: end_shopping(cart_no,conn)
'''	

def end_shopping(cart_no,conn):
    con_iot_db = psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
    cur_iot_db = con_iot_db.cursor()
    cur_iot_db.execute("select* from %s"%(cart_no))
    resultset = cur_iot_db.fetchall()
    if(resultset):
        date = str(datetime.datetime.now().date())
        year = date[0:4]
        month = date[5:7]
        day = date[8:10]
        table = "_".join(("tb",year))
        table = "_".join((table,month))
        table = "_".join((table,day))
        log_table_creator(table)
        invoice_no = invoice_generator(table)
        con_invoice_db = psycopg2.connect(database="invoice_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
        cur_invoice_db = con_invoice_db.cursor()
        for row in resultset:
            cur_invoice_db.execute("insert into %s(invoice_no, cart_no, product_id, product_name, weight, rate, quantity, price, timestamp) values('%s','%s','%s','%s',%f,%f,%d,%f,'%s')"%(table,invoice_no,cart_no,row[0],row[1],row[2],row[3],row[4],row[5],row[6]))
        cur_iot_db.execute("truncate %s"%(cart_no))
        con_invoice_db.commit()
        con_invoice_db.close()
    else:
        invoice_no = '!empty'
    con_iot_db.commit()
    con_iot_db.close()
    conn.send(invoice_no.encode(FORMAT))
    time.sleep(0.2)
	
'''
* Function Name: handle_client
* Input: conn as socket connection, addr as client's socket address
* Output: None 
* Logic: Handles client's incoming requests, interpretes them and calls other fuctions accordingly.
* Example Call: handle_client(conn, addr)
'''

def handle_client(conn, addr):
    print(f"[NEW CONNECTION] {addr} connected.")
    #cart_no = conn.recv(HEADER).decode(FORMAT)
    #sendTPTW(cart_no,conn,db_con=psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432"))
    #connected = True
    #while connected:
    msg_length= conn.recv(HEADER).decode(FORMAT)
    print("length "+msg_length)
    msg=conn.recv(HEADER).decode(FORMAT)
    print(msg)
    #msg_length = msg[22:]
    if msg_length:
        msg_length = int(msg_length)
        cart_no = "tb_"+msg[:10]
        print(cart_no)
        action = msg[11]
        print(action)
        print(f"[{addr}] {msg}")
        p_id1 = msg[13::]
        p_id = p_id1[:13]
        login=msg[:5]
        reg=msg[:7]
        print(login)
        print(reg)
        if(login=="login"):
            print(login)
            login_user(msg,conn,"appuser")
        elif(reg=="appuser"):
            print("reg")
            user_registration(msg,conn,reg)
        else:
            if (action == "1"):

                print(p_id)
                add_item(cart_no, p_id, conn)
            elif (action == "2"):
                remove_item(cart_no, p_id, conn)
            elif (action == '3'):
                send_table(cart_no, conn)
            elif (action == '4'):
                end_shopping(cart_no, conn)
        #conn.send("Msg received".encode(FORMAT))
    conn.close()

   	
'''
* Function Name: start
* Input: None
* Output: None 
* Logic: Starts server. Handles incoming socket connections from multiple clients and process them using threading
* Example Call: start()
'''
def user_registration(msg,conn,table):
    registration=str(msg)
    user=registration.split("-")
    user_name=user[1]
    user_address=user[2]
    user_phone=user[3]
    user_password=user[4]
    user_repassword=user[5]
    print(table)
    print(user_name)
    print(user_address)
    print(user_phone)
    print(user_password)
    print(user_repassword)
    try:
        con = psycopg2.connect(database="user_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
        timestamp = datetime.datetime.utcnow()
        print(con)
        print(table)
        cur = con.cursor()
        cur.execute("INSERT INTO %s (user_name,user_address,user_phone,user_password,user_repassword,timestamp) VALUES ('%s','%s','%s','%s','%s','%s')"%(table,user_name,user_address,user_phone,user_password,user_repassword,timestamp))
        con.commit()
        #sendTPTW(cart_no,conn,con)
    except:
        pass
    conn.send("Registration is successfull".encode(FORMAT))
def login_user(msg,conn,table):
    user_log=str(msg)
    user=user_log.split("-")
    print(user)
    user_id=user[1]
    pas=user[2]

    try:
        con = psycopg2.connect(database="user_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
        timestamp = datetime.datetime.utcnow()
        print(con)
        print(table)
        cur = con.cursor()
        print(cur)
        cur.execute("select user_phone, user_password from %s where user_phone='%s'"%(table,user_id))
        resultset = cur.fetchall()
        if(resultset==0):
            conn.send("user not exist".encode(FORMAT))
        print(resultset)
        id=str(resultset[0][0])
        password=str(resultset[0][1])
        con.close()
        if(pas==password):
            conn.send("logged in successfully".encode(FORMAT))
            cart_table_creator("tb_"+user_id)
        else:
            conn.send("wrong password".encode(FORMAT))

    except:
        pass
    #conn.send("Msg received".encode(FORMAT))
def cart_table_creator(table):
    con = psycopg2.connect(database="iot_db", user="postgres", password="1234", host="127.0.0.1", port="5432")
    print(con)
    print(table)
    cur = con.cursor()
    print(cur)
    print(cur.execute("create table if not exists %s (product_id text,product_name text,weight numeric,rate numeric,quantity integer,price numeric,timestamp timestamp with time zone)"%(table)))
    con.commit()
    con.close()


     
def start():
    server.listen()
    print(f"[LISTENING] Server is listening on {SERVER}")
    while True:
        conn, addr = server.accept()
        thread = threading.Thread(target=handle_client, args=(conn, addr))
        thread.start()
        print(f"[ACTIVE CONNECTIONS] {threading.activeCount() - 1}")


print("[STARTING] server is starting...")
start()
